/*



*/


OscSeq {

	//var test_BUS;
	var val_list;
	var do_send;
	var do_record;
	var timeOffset;
	var osc_rec;

	var path;
	var bus;
	var index;

	var do_play;

	// constructor
	*new { | path, bus, index |
		^super.new.init(path, bus, index)
	}

	init { | p, b, i|

		//test_BUS   = Bus.control(s,1);
		val_list   = List.new;
		do_send    = false;
		do_record  = false;
		timeOffset = Main.elapsedTime;
		do_play    = 0;

		path  = p;
		bus   = b;
		index = i;

		osc_rec = OSCFunc(

			{ arg msg, time, addr, recvPort;



				var val;

				msg.removeAt(0);
				val = msg[index];
				bus.set(val);


				if(do_record==true,{

					val_list.add([Main.elapsedTime-timeOffset, msg]);

					// [index].postln;
				});

		}, path);

	}

	////////////////////////////////////////////////////
	// some setters
	////////////////////////////////////////////////////

	setBus {|b|   bus = b}

	setIndex {|i| index = i}

	setPath {|p|  path = p}

	////////////////////////////////////////////////////
	// activate recording
	////////////////////////////////////////////////////

	record { | a |

		if(a==1,
			{
				val_list.clear;
				do_record = true
			}
		);

		if(a==0, {do_record = false });

	}

	clear {

		val_list.clear

	}

	////////////////////////////////////////////////////
	// play the sequence once
	////////////////////////////////////////////////////

	play {

		do_play    = false;
		do_record  = false;
		do_play    = true;

		if(val_list.size > 1, {
			{

				for(1,val_list.size-1,
					{   arg i;

						var val, t_wait;

						t_wait = val_list[i][0]-val_list[i-1][0];
						t_wait.wait;

						val = val_list[i][1][index];
						bus.set(val);
						// index.postln;
					}
				);

			}.fork;
		})
	}

}

