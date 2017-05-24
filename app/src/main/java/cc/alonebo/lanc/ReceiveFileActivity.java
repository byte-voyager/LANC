package cc.alonebo.lanc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.alonebo.lanc.model.bean.EventReceiveFile;
import cc.alonebo.lanc.model.bean.UdpTransMsg;
import cc.alonebo.lanc.utils.Utils;

/**
 * Created by alonebo on 17-4-7.
 */

public class ReceiveFileActivity extends Activity {

    @BindView(R.id.tv_receive_file_msg)
    TextView tv_receive_file_msg;
    @BindView(R.id.tv_receive_file_sender)
    TextView tv_receive_file_sender;
    @BindView(R.id.bt_receive_file)
    Button bt_receive_file;
    @BindView(R.id.bt_refuse_receive_file)
    Button bt_refuse_receive_file;
    private UdpTransMsg mUdpTransMsg;//这个msg是从其他设备传输过来的


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recivefile);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUdpTransMsg = (UdpTransMsg) bundle.getSerializable(UdpTransMsg.class.getName());
        ArrayList<String> fileName = mUdpTransMsg.getFilePath();
        if (fileName!=null) {
            tv_receive_file_msg.setText("文件名:"+ Utils.getFileName(fileName.get(0)));
        }

        tv_receive_file_sender.setText("发送者:"+ mUdpTransMsg.getSenderName());
    }

    @OnClick(R.id.bt_receive_file)
    public void receiveFile(){
        if (!checkSize()) {//不够空间
            Utils.showToast(this,"空间不够!");
            return;
        }
        EventBus.getDefault().post(new EventReceiveFile().setUdpTransMsg(mUdpTransMsg));
        finish();
    }

    /**
     * 检查文件大小
     */
    private boolean checkSize() {
        ArrayList<Long> fileLength = mUdpTransMsg.getFileSize();
        long totalSize = 0;
        for (int i = 0; i < fileLength.size(); i++) {
            totalSize += fileLength.get(i);
        }
        if (totalSize>=Utils.getInnerSDCardUsableSpec()) return false;
        return true;
    }


    @OnClick(R.id.bt_refuse_receive_file) void refuseReciveFile(){
        Utils.showToast(this,"拒绝接收文件...");
        finish();
    }


}
