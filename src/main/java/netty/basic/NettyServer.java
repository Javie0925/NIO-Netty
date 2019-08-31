package netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

public class NettyServer {
	
	public static void main(String[] args) throws Exception {
		// 1.创建一个线程组：接收客户端连接
		EventLoopGroup boss = new NioEventLoopGroup();
		
		// 2.创建一个线程组：处理网络操作
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		// 3.创建服务器端启动助手：用于配置参数
		ServerBootstrap bootstrap = new ServerBootstrap();
		
		bootstrap.group(boss, workerGroup)   //4. 设置两个线程组
		.channel(NioServerSocketChannel.class) //5. 使用NioServerSocketChannel作为服务端通道的实现 
		.option(ChannelOption.SO_BACKLOG, 123)  //6.设置线程队列中等待连接的个数
		.childOption(ChannelOption.SO_KEEPALIVE, true) //7. 保持活动链接状态
		.childHandler(new ChannelInitializer<SocketChannel>() {// 8.创建一个通道初始化对象
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {// 9.往pipeLine中添加自定义的handler类
				ChannelPipeline pipeline = sc.pipeline();
				pipeline.addLast(new NettyServerHandler());
			}
		});
		// 至此，服务器端准备完毕
		System.out.println("-------Server is ready-------");
		
		// 启动服务器
		ChannelFuture channelFuture = bootstrap.bind(8080).sync(); //10. 绑定端口，非阻塞
		System.out.println("-------Server is starting-------");
		
		// 11.关闭通道，关闭线程组
		channelFuture.channel().closeFuture().sync();
		boss.shutdownGracefully();
		workerGroup.shutdownGracefully();
		
	}
}
