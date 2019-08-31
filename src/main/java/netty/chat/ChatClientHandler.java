package netty.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 客户端业务处理类
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<String>{

	/**
	 * 接收服务器发过来的数据
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String str) throws Exception {
		System.out.println(str);
	}
	
	
	
}
