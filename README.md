ohw-compass-menu

- 使用者輸入 `/menu` 會打開一個 54 格的 GUI（標題：Compass Menu）。

> 此專案預設使用 1.20 API，若機器版本為 1.8.x，可將 `pom.xml` 中 `spigot.version` 改為 `1.8.8-R0.1-SNAPSHOT`（1.8.9 本身沒有 snapshot，1.8.8 能兼容 1.8.9），並刪除 `plugin.yml` 的 `api-version`。
>
> Maven 在 1.8.8 依賴中可能會試圖下載 `net.md-5:bungeecord-chat`，這個 artifact 經常不存在，故在 `pom.xml` 中已將其從 `spigot-api` 依賴中排除。
- 邊緣以灰色玻璃窗格裝飾（在 1.8 使用 `STAINED_GLASS_PANE` 及資料值 7），中央擺放多個目的地羅盤：**pvp、shop、bridge、smp-1、lobby**。
- 每個羅盤有自訂名稱與 lore，點擊時透過 **BungeeCord 插件通道**將玩家送往對應伺服器。

> 伺服器清單 (`SERVERS`) 是公開常數，可依需求修改。

> **注意**：本插件需運行於 BungeeCord 網絡，並確保 Spigot 伺服器啟用了 `BungeeCord` 選項與允許 `BungeeCord` 插件消息。

建置與安裝：

```bash
# 在容器或本機有 Maven 時執行
# 由於使用 Spigot snapshot 依賴，pom.xml 已加上
# spigotmc snapshot repository，確保網路通暢。
mvn clean package
# 之後把 target/ohw-compass-menu-1.0.0.jar 放進伺服器 plugins 資料夾
```

可進一步擴充：
- 自訂伺服器顯示名稱或圖示
- 增加更多按鈕或自訂 GUI 佈局
- 更換玻璃顏色、加入圖示
- 同樣可用 Gradle 或 Kotlin 重寫程式碼
