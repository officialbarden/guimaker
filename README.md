# GUI Maker — Fabric 1.21.8

Sunucu tarafında çalışan, vanilla sandık ekranlarını kullanan güvenli GUI oluşturucu.
Özel ekran/texture paketi gerekmez. Dedicated sunucuda istemcilerin modu kurması
gerekmez; tek oyunculu kullanımda mod ve Fabric API istemciye kurulur.

## Gereksinimler

- Minecraft 1.21.8
- Java 21
- Fabric Loader 0.19.x
- Fabric API 0.136.1+1.21.8

Mod JAR'ını ve Fabric API JAR'ını `mods/` klasörüne koyun.

## Hızlı başlangıç

İlk çalıştırmada `example` adlı örnek GUI ve
`config/guimaker/guis.json` dosyası otomatik oluşturulur.

```mcfunction
/guimaker open example
/guimaker create market 3 Market Menüsü
/guimaker edit market 0
```

Düzenleyicide:

- **Sol tık:** Ana eldeki eşyayı görsel olarak slota kopyalar; eşya tüketilmez.
- **Sağ tık veya shift+tık:** Slotu ve eylemini temizler.
- Oyuncu envanteri GUI açıkken değiştirilemez.
- Slot numaraları soldan sağa **0** ile başlar (6 satırda 0-53).

Eşyanın davranışı komutla atanır:

```mcfunction
/guimaker action set market 0 10 close_gui
/guimaker action set market 0 11 play_sound ui.button.click
/guimaker action set market 0 12 give_item preset.confirm_item
```

İkinci sayfa ve sayfa geçişi:

```mcfunction
/guimaker page add market 1 3 İkinci Sayfa
/guimaker action set market 0 16 open_page 1
/guimaker action set market 1 10 open_page 0
```

## Komutlar

Herkese açık:

```text
/guimaker help
/guimaker list
/guimaker open <gui> [sayfa]
```

OP seviye 2 gerektiren yönetim komutları:

```text
/guimaker create <gui> <1-6 satır> <başlık>
/guimaker delete <gui>
/guimaker edit [gui] [sayfa]
/guimaker page add <gui> <sayfa> <1-6 satır> <başlık>
/guimaker page remove <gui> <sayfa>
/guimaker item set <gui> <sayfa> <slot>
/guimaker item clear <gui> <sayfa> <slot>
/guimaker action set <gui> <sayfa> <slot> <eylem> [parametre]
/guimaker action clear <gui> <sayfa> <slot>
/guimaker reload
```

`item set`, oyuncunun ana elindeki eşyanın tüm veri bileşenlerini (özel ad,
lore, model verisi vb.) JSON'a kaydeder. Bu eşya yalnızca GUI görselidir ve
GUI'den alınamaz.

## İzin verilen eylemler

| Eylem | Parametre | Açıklama |
|---|---|---|
| `no_op` | yok | Dekoratif/davranışsız slot |
| `close_gui` | yok | Menüyü kapatır |
| `open_page` | sayfa numarası | Aynı GUI içinde başka sayfa açar |
| `play_sound` | izinli ses kimliği | Sabit listedeki sesi çalar |
| `give_item` | izinli preset | Sabit listedeki eşyayı verir |

Tab tamamlama, geçerli eylem ve parametreleri gösterir. Kullanıcı girdisi hiçbir
zaman komut veya function olarak çalıştırılmaz.

## Kalıcılık ve sınırlar

- Dosya: `config/guimaker/guis.json`
- Her değişiklik anında atomik olarak kaydedilir.
- Bozuk dosya otomatik olarak `guis.broken-<tarih>.json` adıyla yedeklenir.
- En fazla 128 GUI, GUI başına 64 sayfa, sayfa başına 1-6 satır.
- Son GUI ve bir GUI'nin son sayfası yanlışlıkla silinemez.
- Tıklamalar oyuncu başına 150 ms hız sınırına tabidir.

## Derleme

```bash
./gradlew build
```

Çıktı `build/libs/` klasöründedir.
