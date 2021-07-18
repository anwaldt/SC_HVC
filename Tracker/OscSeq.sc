/*



*/


IMUTracker {

	//var test_BUS;
	var address;

	var osc_function;

	// constructor
	*new { |address, bus|
		^super.new.init(address, bus)
	}

	init { |a, b|

		address  = a;
		bus      = b;



		osc_rec  = OSCFunc(

			{|msg, time, addr, recvPort|
				var pitch, roll, yaw;
				yaw = msg[2]/360+0.5;
				bus.setAt(0,yaw);
				pitch = msg[3]/180 +0.5;
				bus.setAt(1,pitch);
				roll = msg[4]/360+0.5;
				bus.setAt(2,roll);
			}
			, path);

	}

	////////////////////////////////////////////////////
	// some setters
	////////////////////////////////////////////////////

	connect {|add,port|
		address.sendMsg("/sethost", add, port);
	}


}

