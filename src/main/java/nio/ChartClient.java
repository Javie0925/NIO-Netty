package nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ChartClient {
	
	public static void main(String[] args) {
		System.out.println("开启客户端。。。");
		SocketChannel socketChannel = null;
		try{
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			
			// selector
			Selector selector = Selector.open();
			socketChannel.register(selector, SelectionKey.OP_READ);
			if(!socketChannel.connect(new InetSocketAddress(8080))){
				while(!socketChannel.finishConnect()){
					System.out.println("正在尝试连接");
				}
			}
			// 连接成功
			System.out.println(socketChannel.getLocalAddress()+" 连接服务器成功，请输入文字进行对话：");
			// 循环发送数据
			sendMsg(socketChannel);
			// 读取服务器发过来的数据
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while(true){
				int len = socketChannel.read(buffer);
				if(len>0){
					buffer.flip();
					byte[] array = buffer.array();
					System.out.println(new String(array,0,len));
					buffer.clear();
				}
			}
		}catch (Exception e) {

		}finally {
			System.out.println("关闭连接。。。");
			if(socketChannel!=null){
				try {
					socketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void sendMsg(SocketChannel socketChannel) {
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				Scanner scanner = new Scanner(System.in);
				while(scanner.hasNext()){
					// System.out.println("循环");
					String nextLine = scanner.nextLine();
					if("bye".equalsIgnoreCase(nextLine)){
						break;
					}
					buffer.put(nextLine.getBytes());
					try {
						buffer.flip();
						socketChannel.write(buffer);
					} catch (IOException e) {
						e.printStackTrace();
					}
					buffer.clear();
				}
				try {
					socketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
}
