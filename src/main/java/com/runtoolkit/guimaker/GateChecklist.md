# GUI Maker Fabric — Güvenlik Kontrol Listesi

| # | Kapı | Uygulama | Koruma |
|---|---|---|---|
| 1 | Yönetici izni | `PermissionGate` | Oluşturma/düzenleme/silme yalnızca OP 2+ |
| 2 | Kapalı eylem listesi | `ButtonAction`, `ActionAllowlistGate` | Ham komut/function çalıştırılmaz |
| 3 | Tıklama hız sınırı | `RateLimitGate` | Spam ve TPS kötüye kullanımı |
| 4 | Ses izin listesi | `SoundRegistry` | Yalnızca bilinen sesler |
| 5 | Eşya preset listesi | `ItemPresetRegistry` | Serbest NBT/item modifier çalıştırılmaz |
| 6 | Güvenli kalıcılık | `GuiRepository` | Doğrulanan JSON, atomik kayıt, bozuk dosya yedeği |
| 7 | Salt okunur çalışma GUI'si | `GuiMakerScreenHandler` | Görsel eşyalar alınamaz veya çoğaltılamaz |
| 8 | Yetkili görsel editör | `GuiEditorScreenHandler` | Eşyalar tüketilmeden kopyalanır; envanter taşınmaz |

Eylemler `ButtonAction` enum'u ile sınırlıdır. `OPEN_PAGE` yalnızca aynı GUI'deki
kayıtlı sayfaya gider; `PLAY_SOUND` ve `GIVE_ITEM` sabit listeler kullanır.
Oyuncu veya yapılandırma alanı hiçbir yerde Minecraft komutu/function olarak
yorumlanmaz.
