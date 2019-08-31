package netty.myrpc;

import netty.myrpc.client.NettyRPCProxy;
import netty.myrpc.service.HelloNetty;
import netty.myrpc.service.HelloRPC;

public class TestNettyRPC {

	public static void main(String[] args) {
		
		HelloNetty helloNetty =  (HelloNetty)NettyRPCProxy.create(HelloNetty.class);
		System.out.println(helloNetty.hello());
		
		HelloRPC helloRPC = (HelloRPC)NettyRPCProxy.create(HelloRPC.class);
		System.out.println(helloRPC.hello());
	}
}
