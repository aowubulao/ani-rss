package ani.rss.util;

import ani.rss.entity.Config;
import ani.rss.entity.Login;
import ani.rss.entity.MyMailAccount;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConfigUtil {

    public static final Config CONFIG = new Config();

    /**
     * 默认配置
     */
    static {
        String password = MD5.create().digestHex("admin");
        CONFIG.setSleep(15)
                .setRenameSleep(1)
                .setRename(true)
                .setWatchErrorTorrent(true)
                .setFileExist(false)
                .setDelete(false)
                .setOffset(false)
                .setTitleYear(false)
                .setAcronym(false)
                .setAutoDisabled(false)
                .setDownloadPath("")
                .setOvaDownloadPath("")
                .setHost("")
                .setDownload("qBittorrent")
                .setUsername("")
                .setPassword("")
                .setSkip5(true)
                .setDebug(false)
                .setProxy(false)
                .setProxyHost("")
                .setProxyPort(8080)
                .setDownloadCount(0)
                .setMail(false)
                .setMailAddressee("")
                .setMailAccount(
                        new MyMailAccount()
                                .setHost("")
                                .setPort(25)
                                .setFrom("")
                                .setPass("")
                                .setSslEnable(false)
                )
                .setLogin(new Login().setUsername("admin").setPassword(password))
                .setExclude(List.of("720"))
                .setTelegram(false)
                .setTelegramChatId("")
                .setTelegramBotToken("")
                .setTelegramApiHost("https://api.telegram.org")
                .setWebHook(false)
                .setTmdb(false)
                .setWebHookBody("")
                .setWebHookUrl("")
                .setWebHookMethod("POST");
    }

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    /**
     * 获取设置文件夹
     *
     * @return
     */
    public static File getConfigDir() {
        Map<String, String> env = System.getenv();
        String config = env.getOrDefault("CONFIG", "config");
        return new File(config).getAbsoluteFile();
    }

    /**
     * 获取设置文件
     *
     * @return
     */
    public static File getConfigFile() {
        File configDir = getConfigDir();
        return new File(configDir + File.separator + "config.json");
    }

    /**
     * 加载设置
     */
    public static void load() {
        File configFile = getConfigFile();

        if (!configFile.exists()) {
            FileUtil.writeUtf8String(JSONUtil.formatJsonStr(GSON.toJson(CONFIG)), configFile);
        }
        String s = FileUtil.readUtf8String(configFile);
        BeanUtil.copyProperties(GSON.fromJson(s, Config.class), CONFIG, CopyOptions
                .create()
                .setIgnoreNullValue(true));
        LogUtil.loadLogback();
        log.debug("加载配置文件 {}", configFile);
        TorrentUtil.load();
    }

    /**
     * 将设置保存到磁盘
     */
    public static void sync() {
        Boolean mail = CONFIG.getMail();
        MyMailAccount mailAccount = CONFIG.getMailAccount();
        String from = mailAccount.getFrom();
        String pass = mailAccount.getPass();
        String host = mailAccount.getHost();
        String mailAddressee = CONFIG.getMailAddressee();
        if (mail) {
            Assert.notBlank(host, "SMTP地址 不能为空");
            Assert.notBlank(pass, "发件人密码 不能为空");
            Assert.isTrue(Validator.isEmail(mailAddressee, true), "收件人 邮箱格式不正确");
            Assert.isTrue(Validator.isEmail(from, true), "发件人 邮箱格式不正确");
        }

        File configFile = getConfigFile();
        String json = GSON.toJson(CONFIG);
        FileUtil.writeUtf8String(JSONUtil.formatJsonStr(json), configFile);
        LogUtil.loadLogback();
        log.debug("保存配置 {}", configFile);
    }

}
