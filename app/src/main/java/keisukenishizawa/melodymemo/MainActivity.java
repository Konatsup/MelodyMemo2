package keisukenishizawa.melodymemo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Debug;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.format.Time;
import android.widget.Toast;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;


public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("testC");
        System.loadLibrary("testD");
        System.loadLibrary("pitch");
     //   System.loadLibrary("waveSizeRead");
    //

         System.loadLibrary("calc");
        System.loadLibrary("arraySum");
        // loadLibrary("test");
    }

    TextView textStat;
    public String filename= "tmp.wav";
    private MediaPlayer mp;
    private MediaRecorder rec;
    /* 録音先のパス */
    MidiFile midiFile;
    ImageButton record,stop,play,pause,pitchDetect;
    final static int SAMPLING_RATE = 44100;
    private int bufSize;
    private AudioRecord myAR;
    private WaveFile wav1 = new WaveFile();
    private short[] shortData;
    public double[] pitchSTR2 = new double[300000];

    public double[] pitchSTR = new double[10000];
    public TextView tv2;

    int strSize,m,s;
    Time time;
    public String filePath= Environment.getExternalStorageDirectory() + "/MelodyMemo/waves/"+filename;
    public String filePathMidi = Environment.getExternalStorageDirectory() + "/MelodyMemo/sample15.mid";
    public double[] pitchStr;
    public int a,v1,v2;
    public int sum = 2;
    int pitchDataSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String target_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MelodyMemo/waves";
        File dir = new File(target_path);
        if(!dir.exists()){
            dir.mkdirs();
        }

        MidiEvent event = new MidiEvent(100, 100) {
            @Override
            protected int getEventSize() {
                return 0;
            }

            @Override
            public int compareTo(MidiEvent midiEvent) {
                return 0;
            }
        };

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);

        record = (ImageButton)findViewById(R.id.record);
        stop = (ImageButton)findViewById(R.id.stop);
        play = (ImageButton)findViewById(R.id.play);
        pause = (ImageButton)findViewById(R.id.pause);
        pitchDetect = (ImageButton)findViewById(R.id.pitchDetect);


        record.setVisibility(View.VISIBLE);
        stop.setVisibility(View.VISIBLE);
        play.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);

        time = new Time("Asia/Tokyo");
        textStat = (TextView)findViewById(R.id.textStat);

       // TextView tv = (TextView) findViewById(R.id.sample_text);
       // tv.setText(testC());
         tv2 = (TextView) findViewById(R.id.sample_text2);
        tv2.setText(stringFromJNI());


      //  sumMM();

        initAudioRecord();

        // ファイルを作成
        wav1.createFile(filePath);
        Log.d("FILEPATH",filePath);
        //midiMake();

        // スタートボタンのクリックイベントを設定
        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //play.setVisibility(View.GONE);
                //stop.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"録音開始",Toast.LENGTH_SHORT).show();
                textStat.setText("録音中");
                time.setToNow();
                filename = time.year + "_"+ (time.month+1) + "_" + time.monthDay + "-"
                        +time.hour + "_" + time.minute + "_" + time.second + ".wav";
                filePath = Environment.getExternalStorageDirectory() + "/MelodyMemo/waves/"+filename;
                // ファイルを作成
                wav1.createFile(filePath);
                myAR.startRecording();
                myAR.read(shortData, 0, bufSize / 2);

            }

        });

        // 停止ボタンのクリックイベントを設定
        stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                //stop.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                textStat.setText("録音完了");
                Toast.makeText(getApplicationContext(),filename+"で保存しました",Toast.LENGTH_SHORT).show();

                myAR.stop();
            }

        });

       /*record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // ファイルが存在する場合は削除
                play.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                time.setToNow();
                filename = time.year + "_"+ (time.month+1) + "_" + time.monthDay + "-"
                        +time.hour + "_" + time.minute + "_" + time.second + ".wav";
                filePath = Environment.getExternalStorageDirectory() + "/MelodyMemo/waves/"+filename;
                File wavFile = new File(filePath);
                if (wavFile.exists()) {
                    wavFile.delete();
                }
                wavFile = null;
                try {
                    // time = new Time("Asia/Tokyo");
                    Toast.makeText(getApplicationContext(),"録音開始",Toast.LENGTH_SHORT).show();
                    textStat.setText("録音中");
                    rec = new MediaRecorder();
                    rec.setAudioSource(MediaRecorder.AudioSource.MIC);
                    rec.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    rec.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    rec.setOutputFile(filePath);
                    rec.prepare();
                    rec.start();
                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        });*/


        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    textStat.setText("録音停止");
                    Toast.makeText(getApplicationContext(),filename+"で保存しました",Toast.LENGTH_SHORT).show();
                    rec.stop();
                    rec.reset();
                    rec.release();
                    stop.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                    //pause.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    textStat.setText(filename+"を再生");
                    play.setVisibility(View.GONE);
                    stop.setVisibility(View.VISIBLE);
                    mp = new MediaPlayer();
                    mp.setDataSource(filePath);
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener(){
          @Override
            public void onClick(View v){

              mp.pause();
          }
        });

        pitchDetect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    Toast.makeText(getApplicationContext(),"ピッチ抽出中",Toast.LENGTH_SHORT).show();

                    //String sample = filePath;
                    /*for(int i =0;i<3000;i++) {
                        pitchSTR2[i] = i;
                    }*/

                    pitchDataSize = pitch(pitchSTR2,filePath);

                   // pitchSTR2[0]= 100000.0;

                    /*for(int i =0;i<300;i++) {
                        sample = sample + ": " +String.valueOf(pitchSTR2[i]);
                       tv2.setText(sample);

//                        Log.d("PIDJFLDS","asdfas");
                    }*


                   /* progressDialog = new ProgressDialog();
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("処理を実行中しています");
                    progressDialog.setCancelable(true);
                    progressDialog.show();*/
                    midiMake();
                  //  strSize = waveSizeRead(filePath); //
                    //Log.d("SSSSSSSTTTTTTRR",""+strSize);
                    textStat.setText("抽出完了");
                    Toast.makeText(getApplicationContext(),"ピッチ抽出完了",Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Example of a call to a native method
      //  TextView tv = (TextView) findViewById(R.id.sample_text);
       // tv.setText(stringFromJNI());

        /*for(int i =0;i<300000;i++) {
            pitchSTR2[i] = 440;
        }*/


    }//onCreate() finished

    public void onPause(){
        super.onPause();
        wav1.close();
    }

    public void onDestroy(){
        super.onDestroy();
        myAR.release();
    }

    // AudioRecordの初期化
    private void initAudioRecord(){

        // AudioRecordオブジェクトを作成
        bufSize = android.media.AudioRecord.getMinBufferSize(SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        myAR = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufSize);

        shortData = new short[bufSize / 2];

        // コールバックを指定
        myAR.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {

            // フレームごとの処理
            @Override
            public void onPeriodicNotification(AudioRecord recorder) {
                // TODO Auto-generated method stub
                myAR.read(shortData, 0, bufSize / 2); // 読み込む
                wav1.addBigEndianData(shortData); // ファイルに書き出す
            }

            @Override
            public void onMarkerReached(AudioRecord recorder) {
                // TODO Auto-generated method stub

            }
        });

        // コールバックが呼ばれる間隔を指定
        myAR.setPositionNotificationPeriod(bufSize / 2);

    }
/*
public void sumMM(){
    v1 = 1;
    v2 = 2;
    // a = addValues(v1, v2);

    int[] intArray = new int[20];

    intArray[0] = 0;



    for(int i =1; i < 20; i++){
        intArray[i] = 1 + intArray[i-1];
        Log.d("intArrayPre",""+intArray[i -1]);
    }

    sum = Sum(sum, 100);
 //   sum = addValues(sum,100);
    //      sum = Test();

    Log.d("SUM",""+arraySum(intArray));
    for(int i =0; i < 20; i++){
        Log.d("intArray",""+intArray[i]);
    }


}*/


public void midiMake() {
    MidiTrack tempoTrack = new MidiTrack();
    MidiTrack noteTrack = new MidiTrack();

// 2. Add events to the tracks


// Track 1 will have some notes in it
    final int NOTE_COUNT = 80;


    double[] pitchSTR = new double[100];

    for (int i = 0; i < 10; i++) {
        pitchSTR[i] = 0.0;
    }
    for (int i = 10; i < 20; i++) {
        pitchSTR[i] = 440.0;//A4
    }
    for (int i = 20; i < 40; i++) {
        pitchSTR[i] = 493.9;//B4
    }
    for (int i = 40; i < 60; i++) {
        pitchSTR[i] = 392.0;//G4
    }
    for (int i = 60; i < 65; i++) {
        pitchSTR[i] = 0.0;
    }

/*
    for(int i =0; i<10;i++){
        pitchSTR2[i] = 0.0;
    }
    for(int i =10; i<20;i++){
        pitchSTR2[i] = 440.0;//A4
    }
    for(int i =20; i<40;i++){
        pitchSTR2[i] = 523.3;//C5
    }
    for(int i =40; i<60;i++){
        pitchSTR2[i] = 392.0;//G4
    }
    for(int i =60; i<65;i++){
        pitchSTR2[i] = 0.0;
    }*/
       /* for(int i =0; i<100000;i++){
            pitchSTR[i] = 440.0;
        }*/
/*
        for(int i =0; i<10000;i++){
            pitchSTR[i] = 0.0;
        }
        for(int i =10000; i<20000;i++){
            pitchSTR[i] = 440.0;//A4
        }
        for(int i =20000; i<40000;i++){
            pitchSTR[i] = 493.9;//B4
        }
        for(int i =40000; i<60000;i++){
            pitchSTR[i] = 392.0;//G4
        }
        for(int i =60000; i<65000;i++){
            pitchSTR[i] = 0.0;
        }
*/
    final int _framesize = 60; //何Tick入ったら値としてとるかの定数
    int[] pitchSTR3 = new int[pitchDataSize]; //音階が入る(69,70など)
    int[] pitchSTR4 = new int[pitchDataSize / _framesize + 1]; //音階が入る(69,70など)
    int[] midiLength = new int [pitchDataSize/_framesize + 1]; //ノートごとの長さが入る
    int length_count = 0; //一時的なmidiノートの長さを数える変数
    int note_count = 0; //ノートの数
    long[] tickSTR = new long[20];
    int tick_count = 0;
    int value = 0; //60個分連続で同じ音階が入ってるか確かめる変数
    int midiDataSize = pitchDataSize/_framesize;
    double natural = 440.0;
    int sizeValue = 15;
    //A4は69
    int channel = 0;
    int velocity = 100; //固定で100
    int pitch, pitchPre;
    long duration = 0;
    long tick = 0;   //長さ
    int d_count = 0;  //duration Count
    int median;//中央値

    long tickSum4 = 0;
    long tickSum8 = 0;
    long tickSum12 = 0;
    long tickSum16 = 0;
    String sample = "DATASIZE:" + pitchDataSize;

    boolean continue_flg = true; //同じようにつながっているかどうか

    for (int i = 0; i < pitchDataSize; i++) {

        if (pitchSTR2[i] == 0.0) {  //ピッチが0のとき
            pitchSTR3[i] = 0;
        } else {
            pitchSTR3[i] = (int) (12 * (Math.log(pitchSTR2[i] / natural) / Math.log(2)) + 69);
        }
    }

    //Tickから1/32音符の周期で分割
    for (int i = 0; i < midiDataSize; i++) {

        continue_flg = true;
        value = pitchSTR3[i * _framesize];


        for (int j = 0; j < _framesize; j++) {

            if (value != pitchSTR3[i * _framesize + j]) {
                continue_flg = false; //連続で60回(60Tick分)続いていないと判定
            }

            value = pitchSTR3[i * _framesize + j];
        }


        if (continue_flg == false) {
            pitchSTR4[i] = 0;
        } else {
            pitchSTR4[i] = value; //1/32音符(BPM120時)の挿入
        }


    }

    //
    for (int i = 1; i < midiDataSize; i++) {


        if (pitchSTR4[i] >0){ //次のピッチの値を確認してからinsertNoteする

            if (pitchSTR4[i] > pitchSTR4[i - 1]) {
                if (pitchSTR4[i-1] > 0) {
                    noteTrack.insertNote(channel, pitchSTR4[i-1], velocity, tick, duration); //ノートの挿入
                    midiLength[tick_count++] = length_count;
                }

                //リセット
                d_count = 0; //
                tick = i * sizeValue; //スタート地点の指定
                duration = ++d_count * sizeValue; //最低の長さを指定

            } else if (pitchSTR4[i] < pitchSTR4[i - 1]) {
                noteTrack.insertNote(channel, pitchSTR4[i-1], velocity, tick, duration);
                tickSTR[tick_count++] = tick;

                d_count = 0;
                tick = i * sizeValue;//スタート地点の指定
                duration = ++d_count * sizeValue;//最低の長さを指定

            } else { //pitchSTR4[i-1]==pitchSTR4[i]
                length_count++;
//                duration = ++d_count * sizeValue;
            }

        }else if (pitchSTR4[i] == 0 && pitchSTR4[i] < pitchSTR4[i - 1]) {  //ノートが切れたとき
            noteTrack.insertNote(channel, pitchSTR4[i - 1], velocity, tick, duration);
            midiLength[tick_count++] = length_count;
            length_count = 0;
        }

    }


//        sample = sample + ": " +String.valueOf(pitch);
    //      tv2.setText(sample);
        Arrays.sort(midiLength); //長さのソート
        median = midiLength[midiDataSize/2]; //中央の長さをとる


    for (int i = 0; i < midiDataSize; i++) {
        if(pitchSTR4[i])
    }

        for (int i = 1; i < midiDataSize; i++) {


            if (pitchSTR4[i] >0){ //次のピッチの値を確認してからinsertNoteする

                if (pitchSTR4[i] > pitchSTR4[i - 1]) {
                    if (pitchSTR4[i-1] > 0) {
                        noteTrack.insertNote(channel, pitchSTR4[i-1], velocity, tick, duration); //ノートの挿入
                        tickSTR[tick_count++] = tick;
                    }

                    //リセット
                    d_count = 0; //
                    tick = i * sizeValue; //スタート地点の指定
                    duration = ++d_count * sizeValue; //最低の長さを指定

                } else if (pitchSTR4[i] < pitchSTR4[i - 1]) {
                    noteTrack.insertNote(channel, pitchSTR4[i-1], velocity, tick, duration);
                    tickSTR[tick_count++] = tick;

                    d_count = 0;
                    tick = i * sizeValue;//スタート地点の指定
                    duration = ++d_count * sizeValue;//最低の長さを指定

                } else { //pitchSTR4[i-1]==pitchSTR4[i]
                    duration = ++d_count * sizeValue;
                }

            }else if (pitchSTR4[i] == 0 && pitchSTR4[i] < pitchSTR4[i - 1]) {  //ノートが切れたとき
                noteTrack.insertNote(channel, pitchSTR4[i - 1], velocity, tick, duration);
                tickSTR[tick_count++] = tick;
                d_count = 0;
            }

    }


        for(int i = 0; i < pitchDataSize; i++){

           /* if(pitchSTR2[i]==0.0) {  //ピッチが0のとき
                pitch = 0;
            }else{
                pitch = (int) (12 * (Math.log(pitchSTR2[i] / natural) / Math.log(2)) + 69);
            }

            sample = sample + ": " +String.valueOf(pitch);
            tv2.setText(sample);
            if(i!=0) {  //pitchPreの例外処理


                //音の高さの計算
                if(pitchSTR2[i-1]==0.0) {
                    pitchPre = 0;
                }else{
                    pitchPre = (int) (12 * (Math.log(pitchSTR2[i - 1] / natural) / Math.log(2)) + 69);
                }

            //    Log.d("pitchPre",""+pitchPre);
                if (pitch > 0) { //次のピッチの値を確認してからinsertNoteする

                    if (pitch > pitchPre) {
                        if(pitchPre > 0){
                            noteTrack.insertNote(channel, pitchPre, velocity, tick, duration); //ノートの挿入
                            tickSTR[tick_count++] = tick;
                        }
                        //リセット
                        d_count = 0; //
                        tick = i * sizeValue; //スタート地点の指定
                        duration = ++d_count * sizeValue; //最低の長さを指定

                    } else if (pitch < pitchPre) {
                        noteTrack.insertNote(channel, pitchPre, velocity, tick, duration);
                            tickSTR[tick_count++] = tick;

                            d_count = 0;
                            tick = i * sizeValue;//スタート地点の指定
                            duration = ++d_count * sizeValue;//最低の長さを指定

                    } else {//pitchPre==pitch
                        duration = ++d_count * sizeValue;
                    }

                }else if(pitch == 0 && pitch < pitchPre){  //ノートが切れたとき
                    noteTrack.insertNote(channel, pitchPre, velocity, tick, duration);
                    tickSTR[tick_count++] = tick;
                    d_count = 0;
                }

            }
*/

        }
//        Log.d("tickCount",""+tick_count);

      /*  for(int j = 0; j < tick_count-1;j++) {

         //   Log.d("tickSTR[]",j+":"+tickSTR[j]);


            tickSum4 = tickSum4 + (tickSTR[j+1] - tickSTR[j]);
           // Log.d("tickSTR[aaaaaaaa]",""+tickSTR[j+1]);
           // Log.d("tickSum",tickSum4+"");
        }

         long tickAve = tickSum4/(tick_count-1);

  //      Log.d("tickAve",tickAve + "");
*/
        // Track 0 is the tempo map
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo tempo = new Tempo();


        //残りの処理
        /*
        ・正規化(tickとdurationを変化した倍数分乗算する(もう一回配列入れ直し))
        ・倍数
            120を1拍と計算する


         */

        //クォンタイズ機能
        //テンポの指定ができるように

        tempo.setBpm(120);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);


// 3. Create a MidiFile with the tracks we created
        List<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        tracks.add(noteTrack);

        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, (ArrayList<MidiTrack>) tracks);

// 4. Write the MIDI data to a file
        File output = new File(filePathMidi);
        try
        {
            midi.writeToFile(output);
        }
        catch(IOException e)
        {
            System.err.println(e);
        }

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */


    public void pitchDetect(){
        //char[] fileChar = new char[200];
        //fileChar = filePath.toCharArray();
        //strSize = waveSizeRead(filePath); //
        //Log.d("FILEPATH",""+strSize);
       // pitchStr = new double[strSize];
        //Log.v("HelloJni", s);
        // pitch.java_to_c(filePath);
      //  pitchStr = pitch(tmpFile);

    }

    //public native String
//    public static native void pitch(String s);
   // public native int pitch(String s);
    public native String stringFromJNI();
    public native String testC();
    public native String testDD();
   // public static native int test();
  //
  // public native int Test();
  //  public native int addValues(int v1, int v2);
   public native int Sum(int v1, int v2);
    public native int arraySum(int[] arr);
    public native int pitch(double[] arr, String fileName);
    public native int waveSizeRead(String str);

}
