package zladnrms.defytech.kim.BroadcastTv.Netty.Client;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.orhanobut.logger.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import zladnrms.defytech.kim.BroadcastTv.GlobalApplication;
import zladnrms.defytech.kim.BroadcastTv.Packet.ChangeSubjectPacket;
import zladnrms.defytech.kim.BroadcastTv.Packet.ChatPacket;
import zladnrms.defytech.kim.BroadcastTv.Packet.ConnectPacket;
import zladnrms.defytech.kim.BroadcastTv.Packet.EndingPacket;
import zladnrms.defytech.kim.BroadcastTv.Packet.EntryPacket;
import zladnrms.defytech.kim.BroadcastTv.Packet.HeaderPacket;
import zladnrms.defytech.kim.BroadcastTv.eventbus.ChangeSubjectEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.EndingEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;

public class NettyClientHandler extends SimpleChannelInboundHandler<HeaderPacket> {

    /*
     * 0 입장, 1 퇴장, 2 채팅, 3 포지션 선택, 4 게임 준비, 5 게임 준비 취소, 6 게임 시작, 7 포지션 이동, 8 점프
     */
    private HeaderPacket headerPacket;
    private EntryPacket entryPacket;
    private ChatPacket chatPacket;
    private ConnectPacket connectPacket;
    private EndingPacket endingPacket;
    private ChangeSubjectPacket csePacket;

    // 플레이어 설정
    private NettyClient nc;

    // 채팅 리스트뷰 초기화
    private ChatInfo chatInfo;

    private Context context;

    private LocalDataRepository repo;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public NettyClientHandler(Context context) {
        this.context = context;
        repo = LocalDataRepository.getInstance();
    }

    /* Event Bus */
    private void DataSend(Object object) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Logger.d("Main Loop");
        } else {
            if (object instanceof ChatInfo) { /* Send Chatting */
                mHandler.post(() -> {
                    RxBus.get().post("broadcast", object);
                    RxBus.get().post("viewer", object);
                });
            } else if (object instanceof EndingEvent) { /* Send Ending */
                mHandler.post(() -> {
                        RxBus.get().post("viewer", object);
                });
            } else if (object instanceof ChangeSubjectEvent) { /* Send Change Subject */
                mHandler.post(() -> {
                    RxBus.get().post("viewer", object);
                });
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeaderPacket headerPacket) throws Exception {

        int requestCode = headerPacket.getRequestCode();
        Logger.t("channelRead0").d("Packet Code 『" + requestCode + "』");

        int roomId = repo.getUserRoomId(context);

        switch (requestCode) {
            case 0: // 입장 패킷 받아 해석
                entryPacket = (EntryPacket) headerPacket;
                String nickname_0 = entryPacket.getNickname();
                int roomId_0 = entryPacket.getRoomId();
                if (roomId == roomId_0) {
                    chatInfo = new ChatInfo(roomId_0, "알림", nickname_0 + "님이 입장하셨습니다");
                    DataSend(chatInfo);
                }
                break;

            case 1: // 퇴장 패킷 받아 해석
                entryPacket = (EntryPacket) headerPacket;
                int roomId_1 = entryPacket.getRoomId();
                String nickname_1 = entryPacket.getNickname();
                if (roomId == roomId_1) {
                    chatInfo = new ChatInfo(roomId_1, "알림", nickname_1 + "님이 나가셨습니다");
                    DataSend(chatInfo);
                }
                break;

            case 2: // 채팅 패킷 받아 해석
                chatPacket = (ChatPacket) headerPacket;
                int roomId_2 = chatPacket.getRoomId();
                String nickname_2 = chatPacket.getNickname();
                String chat = chatPacket.getChat();
                Logger.t("NettyClientHandler-case2").d(roomId + "d" + roomId_2 + "번방 : " + chat);
                if (roomId == roomId_2) {
                    Logger.t("NettyClientHandler-case2").d(roomId + "번방 : " + chat);
                    chatInfo = new ChatInfo(roomId_2, nickname_2, chat);
                    DataSend(chatInfo);

                    Logger.t("Handler-Bus").d(chatInfo.getChat());
                }
                break;

            case 10:
                csePacket = (ChangeSubjectPacket) headerPacket;
                int roomId_200 = csePacket.getRoomId();
                String subject_200 = csePacket.getSubject();
                if (roomId == roomId_200) {
                    Logger.t("case 200").d(roomId_200 + "번 방, 제목 변경 : " + subject_200);
                    ChangeSubjectEvent cseEvent = new ChangeSubjectEvent(roomId_200, subject_200);
                    DataSend(cseEvent);
                }
                break;

            case 100:
                connectPacket = (ConnectPacket) headerPacket;
                String nickname_100 = connectPacket.getNickname();
                if (roomId == -1) {
                    Logger.t("case 100").d("-1" + " : " + nickname_100 + "유저가 방 입장");
                }
                break;

            case 101:
                endingPacket = (EndingPacket) headerPacket;
                int roomId_101 = endingPacket.getRoomId();
                String nickname_101 = endingPacket.getNickname();
                if (roomId == roomId_101) {
                    Logger.t("case 101").d(roomId_101 + " : " + nickname_101 + "유저의 방송 종료");
                    EndingEvent endingEvent = new EndingEvent(roomId_101, nickname_101);
                    DataSend(endingEvent);
                }
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        Logger.d("Read Complete");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logger.d("channel ACTIVE");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Logger.d("channel INACTIVE");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        cause.printStackTrace();
    }
}