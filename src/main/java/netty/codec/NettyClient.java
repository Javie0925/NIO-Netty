package netty.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class NettyClient {

	public static void main(String[] args) throws InterruptedException {
		// 1.创建一个线程组
		NioEventLoopGroup group = new NioEventLoopGroup();
		// 2.创建客户端的启动助手，完成相关的配置
		Bootstrap bootstrap = new Bootstrap();
		// 3.配置线程组
		bootstrap.group(group)
		.channel(NioSocketChannel.class) // 4.设置客户端通道的实现类
		.handler(new ChannelInitializer<SocketChannel>() { //5.创建一个通道初始化对象
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				// 添加google编码器
				sc.pipeline().addLast("encoder",new ProtobufEncoder());
				// 6.往pipeline链中添加自定义handler
				sc.pipeline().addLast(new NettyClientHandler()); 
			}
		});
		// 至此客户端配置完成
		System.out.println("--------client is ready-------");
		// 7.启动客户端去连接服务端(异步非阻塞)
		ChannelFuture channelFuture = bootstrap.connect("127.0.0.1",8080).sync();
		// 8. 关闭连接(异步非阻塞)
		channelFuture.channel().closeFuture().sync();
		
	}
}
