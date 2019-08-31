package netty.chat;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 服务器端的业务处理类
 * SimpleChannelInboundHandler<String>会将客户端发送过来的数据自动转换成字符串
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

	public static List<Channel> channels = new ArrayList<>();
		
	/**
	 * 通道就绪
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 获取当前通道
		Channel inChannel = ctx.channel();
		// 加入通道列表
		channels.add(inChannel);
		System.out.println("[Server]: "+inChannel.remoteAddress().toString().substring(1)+" 上线了");
	}

	/**
	 * 通道未就绪,通道关闭
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 获取当前通道
		Channel inChannel = ctx.channel();
		// 从列表中移除通道
		channels.remove(inChannel);
		System.out.println("[Server]: "+inChannel.remoteAddress().toString().substring(1)+" 下线了");
	}

	/**
	 * 读取数据
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String str) throws Exception {
		// 获取当前通道
		Channel inChannel = ctx.channel();
		// 广播信息,排除当前通道
		System.out.println("["+inChannel.remoteAddress().toString().substring(1)+"]: "
				+str+"\r\n");
		channels.forEach(channel->{
			if(channel!=inChannel){
				channel.writeAndFlush("["+inChannel.remoteAddress().toString().substring(1)+"]: "
						+str+"\r\n");
			}
		});
	}

	
	
	
	
}
