package netty.myrpc.service.impl;

import netty.myrpc.service.HelloNetty;

public class HelloNettyImpl implements HelloNetty{

	@Override
	public String hello() {
		return "hello,netty";
	}

}
