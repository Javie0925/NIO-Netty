package netty.myrpc.service.impl;

import netty.myrpc.service.HelloRPC;

public class HelloRPCImpl implements HelloRPC{

	@Override
	public String hello() {
		return "hello RPC";
	}

}
