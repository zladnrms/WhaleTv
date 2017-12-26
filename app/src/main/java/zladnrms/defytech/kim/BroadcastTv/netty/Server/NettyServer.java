package zladnrms.defytech.kim.BroadcastTv.netty.Server;

import java.util.ArrayList;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    private ArrayList<ServerUserInfo> serverUserInfoArr = new ArrayList<ServerUserInfo>(); // 유저 개개인 정보

    private int port = 6060;

    private NettyServerInitializer nsi;

    public NettyServer() {
        // SingleTone
    }

    private static NettyServer instance = new NettyServer(); // SingleTone

    public static NettyServer getInstance() {  // SingleTone
        return instance;
    }

    public void run() throws Exception {

        nsi = new NettyServerInitializer();

        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3
                    .childHandler(nsi)
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            System.out.println("◈『WhaleTv』서버 OPEN");

            ChannelFuture f = b.bind(port).sync(); // (7)

            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

            System.out.println("◈『WhaleTv』서버 CLOSE");
        }
    }

    public ArrayList<ServerUserInfo> getServerUserInfoArr() {
        return serverUserInfoArr;
    }

    public static void main(String[] args) throws Exception {
        new NettyServer().run();
    }
}