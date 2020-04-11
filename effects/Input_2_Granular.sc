/*

Capture the input in a buffer
and play it back using a grain thing

fragments for use with a booted server

*/



////////////////////////////////////////////////////////////////////////////////////////////////////////////
// SYNTHDEFS
////////////////////////////////////////////////////////////////////////////////////////////////////////////


(

SynthDef(\feedback_looper, {

	arg input, bufnum, t_reset = 1;

	// variables for the existing signal in the loop, the new input,
	// the output signal and the recording head position
	var inputSig, outputSig, existingSig, recHead;

	// get the input signal
	inputSig = In.ar(input);

	// generate the recording (also playback) position
	recHead = Phasor.ar(t_reset, BufRateScale.kr(bufnum), 0, BufFrames.kr(0));

	// read the existing signal from the loop
	// existingSig = BufRd.ar(1, bufnum, recHead);

	// put the existing signal plus the new signal into the loop
	// BufWr.ar(inputSig + existingSig, bufnum, recHead);
	BufWr.ar(inputSig, bufnum, recHead);

	// play back signal we got from the loop before the writing operation
	// Out.ar(0, existingSig);
}).add;


SynthDef(\feedback_scroller,
	{
		|
		out      = 1,
		buffer   = 0,
		envbuf   = -1,
		pointer  = 0,
		pitch    = 1,
		win_size = 0.2
		|

		var  outsig;

		outsig = Warp1.ar(
			numChannels:1,
			bufnum:buffer,
			pointer:pointer,
			freqScale:pitch,
			windowSize:win_size,
			envbufnum:envbuf,
			overlaps:8,
			windowRandRatio:0,
			interp:2);

		Out.ar(out, outsig);

}).add;


SynthDef(\feedback_buffer, {
	|
	out      = 0,
	bufnum   = 0,
	sound_in = 1
	|

	var input;

	input = SoundIn.ar(sound_in);
	RecordBuf.ar(input, bufnum);

}).add;


)



////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CONTROL
////////////////////////////////////////////////////////////////////////////////////////////////////////////

(

// mouse xy controll with busses
~tmp_BUS = Bus.control(s,2);

~mouse   = {
	Out.kr(~tmp_BUS.index,   MouseX.kr(0.01,4));
	Out.kr(~tmp_BUS.index+1, MouseY.kr(0.01,2));
}.play;


)



////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create!
////////////////////////////////////////////////////////////////////////////////////////////////////////////

~input_GROUP = Group.head(s);

(

if(~input_GROUP!=nil,
	{

		~feedback_BUFFER = Buffer.alloc(s, 44100 * 2, 1); // a four second 1 channel Buffer
		~feedback_loop   = Synth.new(\feedback_looper,  [
			\input, 9,
			\buffnum, ~feedback_BUFFER.bufnum],
		target:~input_GROUP);
		~feedback_loop.set(\t_reset,1);


		~feedback_helper =Synth.new(\feedback_buffer,
			[
				\out, 0,
				\bufnum, ~feedback_BUFFER.bufnum
		],  target:~input_GROUP);


		// b = Buffer.read(s,"/home/anwaldt/Desktop/Live_Set_2019/WAV/djembe1.wav");
		~feedback_scroller = Synth.new(\feedback_scroller,nil, target:~input_GROUP);

		// a custom envelope
		~winenv = Env([0, 1, 0], [0.25, 0.25]);

		// winenv =  LFGauss.ar(0.1, 0.12);

		z = Buffer.sendCollection(s, ~winenv.discretize, 1);

		~feedback_scroller.set(\envbuf, z);
		~feedback_scroller.map(\pointer,  ~tmp_BUS.index);
		~feedback_scroller.map(\win_size, ~tmp_BUS.index);
		~feedback_scroller.map(\pitch, ~tmp_BUS.index+1);
	},
	{
		postln("No instrument group!");
	}
)

)

(
~feedback_scroller.free;
~feedback_BUFFER.free;
~feedback_loop.free;
~feedback_helper.free;

)
////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  OSC ////////////////////////////////////////////////////////////////////////////////////////////////////////////



(

~pitch_OSC = OSCFunc(

	{ arg msg, time, addr, recvPort;

		~feedback_scroller.set(\pitch, msg[1]);
}, '/bong/imu/pitch');


~size_OSC = OSCFunc(

	{ arg msg, time, addr, recvPort;

		~feedback_scroller.set(\win_size, msg[1]);
}, '/bong/imu/roll');


~pointer_OSC = OSCFunc(

	{ arg msg, time, addr, recvPort;

		~feedback_scroller.set(\pointer, msg[1]);
}, '/bong/imu/head');

)





////////////////////////////////////////////////////////////////////////////////////////////////////////////
// automatic scroll!
////////////////////////////////////////////////////////////////////////////////////////////////////////////

(

y = {|out = 0|

	//		((SinOsc.kr(0.5)+1) *0.8)
	((LFSaw.kr(0.1, 1)+1)*1)
}.play;


~feedback_scroller.map(\pointer, 0);

)

y.free

