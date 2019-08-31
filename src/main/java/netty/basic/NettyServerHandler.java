package netty.basic;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 服务器端的业务处理类
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

	/**
	 * 读取数据事件
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("server context:"+ctx);
		ByteBuf buf = (ByteBuf) msg;
		System.out.println("客户端发来的消息： " + buf.toString(CharsetUtil.UTF_8));
	}
	/**
	 * 数据读取完毕事件
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// 向服务端发送信息
		ctx.writeAndFlush(Unpooled.copiedBuffer("数据读取完毕", CharsetUtil.UTF_8));
	}

	/**
	 * 异常发生事件
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// 关闭通道
		cause.printStackTrace();
		ctx.close();
	}
	
	
	
}
