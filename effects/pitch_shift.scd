/*

A pitch_shifter Synthdef

*/


(

SynthDef(\pitch_shifter,
	{
		|
		input_1     = nil,
		output_1    = nil,
		shift       = 1,
		window_size = 0.1
		|

		var y = PitchShift.ar(In.ar(input_1),	window_size, shift,  0, 0.00);

		Out.ar(output_1,y);

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
	Out.kr(~tmp_BUS.index+1, MouseY.kr(0.001,1));
}.play;


)


////////////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////////////////

(

x = Synth(\pitch_shifter,
	[
		\input_1,  s.options.numOutputBusChannels,
		\output_1,  0,
	]
);


x.map(\shift,       ~tmp_BUS.index);
x.map(\window_size, ~tmp_BUS.index+1);

)

x.set(\window_size,1);



