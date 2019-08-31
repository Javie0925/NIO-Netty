package netty.myrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import netty.myrpc.server.ClassInfo;

public class NettyRPCProxy {
	
	// 根据接口创建代理对象
	public static Object create(Class target){
		return Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				// 封装classInfo
				ClassInfo classInfo = new ClassInfo();
				classInfo.setClassName(target.getName());
				classInfo.setMethodName(method.getName());
				classInfo.setObjs(args);
				classInfo.setTypes(method.getParameterTypes());
				
				//=============================使用netty发送数据=============================
				// 1.创建一个线程组
				NioEventLoopGroup group = new NioEventLoopGroup();
				ResultHandler resultHandler = new ResultHandler();
				// 2.创建客户端的启动助手，完成相关的配置
				try {
					Bootstrap bootstrap = new Bootstrap();
					// 3.配置线程组
					bootstrap
						.group(group)
						.channel(NioSocketChannel.class) // 4.设置客户端通道的实现类
						.handler(new ChannelInitializer<SocketChannel>() { //5.创建一个通道初始化对象
							@Override
							protected void initChannel(SocketChannel sc) throws Exception {
								// 6.往pipeline链中添加自定义handler
								ChannelPipeline pipeline = sc.pipeline();
								// 添加编码器
								pipeline.addLast("encoder",new ObjectEncoder());
								// 添加解码器
								pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE,
										ClassResolvers.cacheDisabled(null)));
								// 添加自定义handler位置在编/解码器之后
								pipeline.addLast("handler",resultHandler); 
								
							}
						});
					// 至此客户端配置完成
					// 7.启动客户端去连接服务端(异步非阻塞),connect()是异步的，sync()是同步阻塞的
					ChannelFuture channelFuture = bootstrap.connect("127.0.0.1",8080).sync();
					// 获取通道
					Channel channel = channelFuture.channel();
					// 发送数据
					channel.writeAndFlush(classInfo);
					
					// 8. 关闭连接(异步非阻塞)
					channelFuture.channel().closeFuture().sync();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					group.shutdownGracefully();
				}
				return resultHandler.getReponse();
			}
		});
	}
}
