package netty.chat;

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
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;

public class ChatServer {
	
	private int port; // 服务端端口号
	
	public ChatServer(int port) {
		super();
		this.port = port;
	}


	public void run() {
		// 1.创建一个线程组：接收客户端连接
		EventLoopGroup boss = new NioEventLoopGroup();
		
		// 2.创建一个线程组：处理网络操作
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			// 3.创建服务器端启动助手：用于配置参数
			ServerBootstrap bootstrap = new ServerBootstrap();
			
			bootstrap.group(boss, workerGroup)   //4. 设置两个线程组
			.channel(NioServerSocketChannel.class) //5. 使用NioServerSocketChannel作为服务端通道的实现 
			.option(ChannelOption.SO_BACKLOG, 128)  //6.设置线程队列中等待连接的个数
			.childOption(ChannelOption.SO_KEEPALIVE, true) //7. 保持活动链接状态
			.childHandler(new ChannelInitializer<SocketChannel>() {// 8.创建一个通道初始化对象
				@Override
				protected void initChannel(SocketChannel sc) throws Exception {
					// 9.往pipeLine中添加自定义的handler类
					ChannelPipeline pipeline = sc.pipeline();
					// 添加编码器
					pipeline.addLast("decoder",new StringDecoder());
					// 添加解码器
					pipeline.addLast("encoder",new StringEncoder());
					pipeline.addLast(new ChatServerHandler());
				}
			});
			// 至此，服务器端准备完毕
			System.out.println("-------Server is ready-------");
			
			// 启动服务器//10. 绑定端口，非阻塞
			ChannelFuture channelFuture = bootstrap.bind(8080).sync(); 
			System.out.println("-------Server is starting-------");
			
			// 11.关闭通道，关闭线程组
			channelFuture.channel().closeFuture().sync();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(boss!=null){
				
				boss.shutdownGracefully();
			}
			if(workerGroup!=null){
				
				workerGroup.shutdownGracefully();
			}
		}
	}
	public static void main(String[] args) {
		new ChatServer(8080).run();
	}
}
