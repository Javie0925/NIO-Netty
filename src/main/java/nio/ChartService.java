package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChartService {
	
	public void start(){
		ServerSocketChannel serverSocketChannel = null;
		try{
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.bind(new InetSocketAddress(8080));
			// selecter
			Selector selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			while(true){
				if(selector.select(1000)>0){
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = keys.iterator();
					while(iterator.hasNext()){
						SelectionKey next = iterator.next();
						if(next.isAcceptable()){
							// 接收连接，并添加到selector里
							SocketChannel sc = serverSocketChannel.accept();
							System.out.println(sc.getRemoteAddress()+" 已上线");
							sc.configureBlocking(false);
							sc.register(selector, SelectionKey.OP_READ);
						}else if(next.isReadable()){
							// 接收数据
							SocketChannel sc = (SocketChannel)next.channel();
							ByteBuffer msg = readMsg(sc);
							// 广播发送数据
							if(msg!=null)
								broadcast(msg,selector,sc);
						}
						// 移除已经处理过的channel
						iterator.remove();
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				serverSocketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 广播发送数据给客户端
	 * @throws IOException 
	 */
	private void broadcast(ByteBuffer msg, Selector selector, SocketChannel sc) throws IOException {
		
		Iterator<SelectionKey> iterator = selector.keys().iterator();
		while(iterator.hasNext()){
			SelectableChannel channel = iterator.next().channel();
			if(channel instanceof SocketChannel && channel!=sc){
				((SocketChannel) channel).write(msg);
			}
		}
	}
	/**
	 * 获取客户端发送过来的数据
	 */
	private ByteBuffer readMsg(SocketChannel sc){
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		try {
			int len = sc.read(buffer);
			buffer.flip();
			System.out.println(sc.getRemoteAddress()+": ");
			System.out.println(new String(buffer.array(),0,len));
		} catch (IOException e) {
			try {
				System.out.println(sc.getRemoteAddress()+" 关闭连接");
				sc.close();
			} catch (IOException e1) {
				e.printStackTrace();
			}
			return null;
		
		}
		return buffer;
	}
	
	public static void main(String[] args) {
		System.out.println("开启服务器。。。");
		ChartService service = new ChartService();
		service.start();
		System.out.println("关闭服务器。。。");
	}
}

