//   one synth listening to the input level
// - triggers on threshold
// - starts recording in buffer
// -
//

/////////////////////////////////////////////////////////////////////////////////////
// sSynthdefs
/////////////////////////////////////////////////////////////////////////////////////


(

SynthDef(\ampSynth,
	{
		|inbus, thresh = 0.5, decay = 1|

		var  trig, amp;

		 amp = Amplitude.kr(In.ar(inbus, 1), attackTime: 0.01, releaseTime: decay);


		trig = HPZ1.kr(amp >= thresh);

		// this trigger gets sent only when amplitude crosses threshold
		SendReply.kr(trig, '/amptrig', trig);

}).add;



SynthDef(\scroller,
	{
		|out = 1, buffer=0, envbuf = -1, pointer = 0, win_size = 0.1, pitch =1, position=0|

		var  outsig;
		// pointer - move from beginning to end of soundfile over 15 seconds

		//pointer   = MouseX.kr(0, 1);

		// LFSaw.ar(1/15).range(0, 1);
		// control pitch with MouseX
		//    pitch = MouseX.kr(0.5, 2);

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

)

/////////////////////////////////////////////////////////////////////////////////////
// OSC stuff
/////////////////////////////////////////////////////////////////////////////////////


(
~oscTrigResp = OSCFunc({ |msg|

	"TRIGGER!".postln;

	~trigger_BUS.set(msg[3]);

	msg[3].postln;

	SynthDef(\recordBuf, { arg out = 0, bufnum = 0;
		//var formant;
		//formant = Formant.ar(XLine.kr(400,1000, 4), 2000, 800, 0.125);
		RecordBuf.ar(In.ar(2), bufnum, doneAction: Done.freeSelf, loop: 0 );
	}).play(s,[\out, 0, \bufnum, b]);


}, '/amptrig', s.addr);
)



////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CONTROL
////////////////////////////////////////////////////////////////////////////////////////////////////////////

(

// mouse xy controll with busses
~tmp_BUS = Bus.control(s,2);

~mouse   = {
	Out.kr(~tmp_BUS.index,   MouseX.kr(0.01,4));
	Out.kr(~tmp_BUS.index+1, MouseY.kr(0.001,2));
}.play;


)


/////////////////////////////////////////////////////////////////////////////////////
// buffers and busses
/////////////////////////////////////////////////////////////////////////////////////

(
// the actual audio buffer
b = Buffer.alloc(s, s.sampleRate * 5, 1);

// the trigger bus
~trigger_BUS = Bus.control(s,1);

)
// record for four seconds

/////////////////////////////////////////////////////////////////////////////////////
// create
/////////////////////////////////////////////////////////////////////////////////////

(


~s1 = Synth.new(\ampSynth, [ inbus: s.options.numOutputBusChannels, thresh: 0.1, decay: 2]);

x = Synth.new(\scroller);

x.map(\position, ~tmp_BUS.index);
x.map(\pitch, ~tmp_BUS.index+1);

)

x.free
~s1.free

