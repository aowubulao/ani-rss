package ani.rss.action;

import ani.rss.annotation.Auth;
import ani.rss.annotation.Path;
import ani.rss.entity.Ani;
import ani.rss.util.AniUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Auth
@Path("/rss")
public class RssAction implements BaseAction {
    @Override
    public void doAction(HttpServerRequest req, HttpServerResponse res) throws IOException {
        if (!req.getMethod().equals("POST")) {
            return;
        }
        Ani ani = getBody(Ani.class);
        String url = ani.getUrl();
        String type = ani.getType();
        String title = ani.getTitle();
        Assert.notBlank(url, "RSS地址 不能为空");
        if (!ReUtil.contains("http(s*)://", url)) {
            url = "https://" + url;
        }
        url = URLUtil.decode(url, "utf-8");
        try {
            Ani newAni = AniUtil.getAni(url, title, type);
            resultSuccess(newAni);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.debug(e.getMessage(), e);
            resultErrorMsg("RSS解析失败 {}", e.getMessage());
        }
    }
}
