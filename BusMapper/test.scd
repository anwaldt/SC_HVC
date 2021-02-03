s.boot;

(
a = Bus.control(s);
b = Bus.control(s);
c = Bus.control(s);
)

(
x = { arg in_bus, gain, offset;
	Out.kr(c, BusMapper.kr(In.kr(in_bus), gain, offset));
}.play
)

x.set(\in_bus, a, \gain,1, \offset,0)

s.scope(4,a.index)

a.set(1)
b.set(0.2)
c.set(0)

a.scope
b.scope
c.scope
