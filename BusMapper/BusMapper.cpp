#include "SC_PlugIn.h"

// pointer to host functions
static InterfaceTable *ft;

// unit generator state
struct BusMapper : public Unit
{
};

static void BusMapper_next(BusMapper* unit, int inNumSamples);
static void BusMapper_Ctor(BusMapper* unit);

// the constructor
void BusMapper_Ctor(BusMapper* unit) {

    //  initialize state variables:

    // set calculation function(s):
    SETCALC(BusMapper_next);

    // calculate one sample of output.
    BusMapper_next(unit, 1);
}


void BusMapper_next(BusMapper* unit, int inNumSamples) {

    // wire buffers:
    float in         = IN0(0);  // first input
    float gain       = IN0(1);  // second input
    float offset     = IN0(2);  // second input
    float  *outBuf = OUT(0); // first output

    for (int i = 0; i < inNumSamples; i++) {
        outBuf[i] = gain;//0.5;//= in  * gain ;
    }
}

//   entry point
PluginLoad(BusMapperUGens) {
    // store pointer to InterfaceTable:
    ft = inTable;
    //   defining the ugen:
    DefineSimpleUnit(BusMapper);
}
