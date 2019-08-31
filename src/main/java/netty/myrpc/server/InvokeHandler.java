package netty.myrpc.server;

import java.lang.reflect.Method;
import java.util.Set;

import org.reflections.Reflections;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InvokeHandler extends ChannelInboundHandlerAdapter{

	/**
	 * 得到某接口下某个实现类的名字
	 * @throws Exception 
	 */
	private String getImplClassName(ClassInfo classInfo) throws Exception{
		// 服务方接口和实现类所在的 包路径
		/*String interfacePath = "netty.myrpc.service";
		String interfaceName = interfacePath.substring(lastDot);*/
		
		int lastDot = classInfo.getClassName().lastIndexOf(".");
		Class superClass = Class.forName(classInfo.getClassName());
		// 使用Reflections类获取接口路径下所有的实现类
		Reflections reflections = new Reflections(classInfo.getClassName().substring(0,lastDot));
		Set<Class> implClassSet = reflections.getSubTypesOf(superClass);
		if(implClassSet.size()==0){
			System.out.println("[InvokeHandler]未找到实现类");
			return null;
		}else if(implClassSet.size()>1){
			System.out.println("[InvokeHandler]找到多个实现类，未明确使用哪一个");
			return null;
		}else{
			// 返回实现类的类名
			Class[] array = implClassSet.toArray(new Class[0]);
			return array[0].getName();
		}
	}

	/**
	 * 读取客户端发来的数据并通过反射调用实现类的方法
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("["+ctx.channel().remoteAddress()+"]" + "已连接");
		ClassInfo classInfo = (ClassInfo)msg;
		String implClassName = getImplClassName(classInfo);
		Object clazz = Class.forName(implClassName).newInstance();
		Method method = clazz.getClass().getMethod(classInfo.getMethodName(), classInfo.getTypes());
		Object result = method.invoke(clazz, classInfo.getObjs());
		ctx.writeAndFlush(result);
	}
	
	
}
