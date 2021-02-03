BusMapper : UGen {
    *kr { |in, gain, offset|
        ^this.multiNew('control', in, gain, offset);
    }
    checkInputs {
        [0, 1].do { |i|
            (inputs[i].rate != 'control').if {
                ^"input % is not control rate".format(i).throw;
            };
        };
        ^this.checkValidInputs;
    }
}