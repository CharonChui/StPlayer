package com.st.stplayer.data

import java.util.*

class TestData {
    companion object {
        private const val SAMPLE_PATH =
            "http://vod1.nty.tv189.com/6/ol/st02/2020/08/07/Q600_fdac4ab3-7ab1-4e15-9add-9c86c967af85.mp4"
        private const val DASH_PATH =
            "http://bitmovin-a.akamaihd.net/content/dataset/multi-codec/stream.mpd"

        fun getSimpleData(): VideoEntity {
            val url = SAMPLE_PATH
            val cover = ""
            return VideoEntity(url, url, cover)
        }

        fun getDashData(): VideoEntity {
            val url = DASH_PATH
            val cover = ""
            return VideoEntity(url, url, cover)
        }

        private fun createLiveVideoEntity(
            title: String,
            url: String
        ): VideoEntity {
            return VideoEntity(title, url, "")
        }

        private fun createMiniVideoEntity(
            title: String,
            url: String,
            cover: String
        ): VideoEntity {
            return VideoEntity(title, url, cover)
        }

        fun getLiveTestData(): List<VideoEntity> {
            val list: MutableList<VideoEntity> =
                ArrayList()
            list.add(createLiveVideoEntity("CCTV-1", "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-2", "http://ivi.bupt.edu.cn/hls/cctv2hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-3", "http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-4", "http://ivi.bupt.edu.cn/hls/cctv4hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-5", "http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-6", "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-7", "http://ivi.bupt.edu.cn/hls/cctv7hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-8", "http://ivi.bupt.edu.cn/hls/cctv8hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-9", "http://ivi.bupt.edu.cn/hls/cctv9hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-10", "http://ivi.bupt.edu.cn/hls/cctv10hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-12", "http://ivi.bupt.edu.cn/hls/cctv12hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-14", "http://ivi.bupt.edu.cn/hls/cctv14hd.m3u8"))
            list.add(createLiveVideoEntity("CCTV-17", "http://ivi.bupt.edu.cn/hls/cctv17hd.m3u8"))
            return list
        }

        fun getMiniTestData(): List<VideoEntity> {
            val list: MutableList<VideoEntity> =
                ArrayList()
            list.add(
                createMiniVideoEntity(
                    "日本10大丧心病狂恶整综艺，这玩笑真的开大了！",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/06/25/258211E38DE744C0BC19803D023A41C2/index.m3u8?ifsign=1",
                    "http://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_25/BDBB4C09FA364230BEBC329295AD665A_w720_h1280.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "占道转弯酿事故，烟雾中走来女司机！",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/24/p37012990-102-009-172425/index.m3u8?ifsign=1",
                    "http://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_35/76DD6239FBBB490795116728B0913993_w512_h1024.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "80%以上80、90后将没钱养老了？",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/26/p37041690-102-009-144925/index.m3u8?ifsign=1",
                    "http://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_35/AC39A99B2B8E4757B9B31742DB278CFC_w360_h640.jpg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "坏主人玩弄猫咪，猫咪都懵圈了",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/07/16/p36557043-102-009-162625/index.m3u8?ifsign=1",
                    "http://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_29/06D1A7071CD24634805A043B9D275641_w480_h852.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "90后小伙租住在廉租房，却网贷15万打赏女主播，没钱后惨遭拉黑",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/13/p36932792-102-009-184325/index.m3u8?ifsign=1",
                    "http://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_33/F35C3315C56D43E2B619D195478D21CD_w512_h1024.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "战狼：不要小看中国，当你遇到的时候，就知道面对的是什么了",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/07/14/EE5F0FB3EF6B49A09E2FB1C40CE8FB1A/index.m3u8?ifsign=1",
                    "http://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_29/771FE8B75A444049BF1C41891EA29C41_w720_h1440.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "PS教学：拯救一张欠曝的照片！",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/24/p37013384-102-009-183725/index.m3u8?ifsign=1",
                    "http://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_35/301137A926864605AD68A60AE9E34F36_w360_h640.jpg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "看见主人回来就超级开心的狗子，根本控制不住自己",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/07/16/p36556579-102-009-154825/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_29/98F82002DBBC4C5C8C3A4BD284738A4E_w480_h852.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "小朋友每天在家练习4分钟，练好体能又长个！",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/23/p37007524-102-009-153025/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_34/C9EB9FF2A0B64DECA6DA207D55A991A4_w640_h1281.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "张雨绮影视清纯混剪，各种妆容变化演绎任何角色都能hold的住",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/07/15/D54DAA0E2DC7466BB2A1C82FD2447272/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_29/46E54320EF7B4672928E71D27697BCE9_w720_h1440.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "皮秒和超皮秒的区别？哪些人可以做？小科普时间到！",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/07/17/p36571705-102-009-143325/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w374_h600_q100_webp/x0.ifengimg.com/thmaterial/2020_29/0BBC6397155841859C3CA4EEA23DAA44_w320_h640.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "有些宝贝刚出生就过上了退休生活，小日子相当的安逸",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/14/p36964659-102-009-155825/index.m3u8?ifsign=1",
                    "http://d.ifengimg.com/w300_h481_blur_q100_webp/x0.ifengimg.com/thmaterial/2020_33/6CC2D103849E4B1FB982C0A077F24029_w240_h481.png.webp"
                )
            )
            return list
        }

        fun getVideoTestData(): List<VideoEntity> {
            val list: MutableList<VideoEntity> =
                ArrayList()
            list.add(
                createMiniVideoEntity(
                    "为了“纪念”9·11，美国派F-35炸平了伊拉克的一个岛",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/15/s46972176-102-9987642-190634/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w399_h200_blur_q100_webp/x0.ifengimg.com/res/2020/F1C6F2FFA7C8F3E2B8B7BBC85B747ED4B2CEDA21_size27_w320_h200.jpeg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "美对台出售F-16V有何玄机？宋忠平：三代半战机卖出了四代机的价格",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/25/p37015130-102-009-152125/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w690_h346_q100_webp/x0.ifengimg.com/ucms/2020_35/F795370D58E0E0875A3845345A4CA998CB0F1418_w640_h360.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "我军两栖战力初步成型，60艘两栖巨舰，装一个集团军也不在话下",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/07/31/s46400941-102-9987642-162734/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w399_h200_blur_q100_webp/x0.ifengimg.com/res/2020/114B439782904202F2760C3562E731000D407A10_size32_w320_h200.jpeg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "美国频繁对台军售有何目的？岛内舆论说出美打“台湾牌”三大原因",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/25/p37062478-102-009-152025/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w505_h253_blur_q100_webp/x0.ifengimg.com/ucms/2020_35/5C3C7470A8CB2208DC5CDE379F3CF8EB8B1980A9_w450_h253.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "美军舰队陷入危险中，关键资料惨遭泄露，损失的可能不止一艘航母",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/07/30/s46334947-102-9987642-174034/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w399_h200_blur_q100_webp/x0.ifengimg.com/res/2020/E8B9386D4A68E3425BC7DD2924B0471210648578_size38_w320_h200.jpeg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "天天来南海搞事的美军侦察机，是怎样和无人机协同作战的？",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/16/s46938351-102-9987642-113634/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w399_h200_blur_q100_webp/x0.ifengimg.com/res/2020/EF8570BB2224987D6B10DE77946F7C36AF990214_size21_w320_h200.jpeg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "中国空军高调飞越所谓“台海中间线”，宋忠平：这条线早已不复存在",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/25/p37015111-102-009-151925/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w560_h281_blur_q100_webp/x0.ifengimg.com/ucms/2020_35/C473683C5895458DAE1A8DD0C33A0D2CADA59114_w500_h281.png.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "大陆有能力“武统”为何一直不动手？台专家一针见血",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/25/p37064470-102-009-212225/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w690_h346_q100_webp/x0.ifengimg.com/ucms/2020_35/7B51D6F67F2239E5E04F7BB3F8449F8E72C83D55_w650_h366.jpg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "中国4万吨小航母出海试航，统一祖国必不可少的利器",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/14/s46892561-102-9987642-162034/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w690_h346_q100_webp/x0.ifengimg.com/res/2020/44355EF55D355A6B1A1F4C57D317B9BA4A673A9F_size20_w696_h391.jpeg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "中国“黑科技”摧毁了美国人的骄傲，歼-20红外隐形超越F-22",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/12/s46803321-102-9987642-113509/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w515_h258_blur_q100_webp/x0.ifengimg.com/res/2020/2045C6F503ED87319A66C451D6038765A9ADAA7A_size16_w460_h258.jpeg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "大片！海军陆战队8轮装甲车版科目二考核",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/21/p37029847-102-009-143625/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w690_h346_q100_webp/x0.ifengimg.com/ucms/2020_34/E8CAF280DF09996E87D6EAD953261C031B2A04D7_w800_h450.jpg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "世界上所有的核武器放在一起引爆，有能力“毁掉地球”吗？",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/09/s46733051-102-9987642-192834/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w399_h200_blur_q100_webp/x0.ifengimg.com/res/2020/5F68D751B76BF105775489B977430E34A2B3B959_size31_w320_h200.jpeg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "中国出现的首艘航母，澳洲接收英航母成精锐，最终却被广东人拆掉",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/07/31/s46402203-102-9987642-175134/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w399_h200_blur_q100_webp/x0.ifengimg.com/res/2020/9B1045C459B95522CD85B62C5C7A070C4E7CC5DC_size31_w320_h200.jpeg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "特朗普为何频繁打“台湾牌”？宋忠平：这张牌“零成本”",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/25/p37015015-102-009-151025/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w596_h299_blur_q100_webp/x0.ifengimg.com/ucms/2020_35/946E2CD6F82B88258146A67EE0FBAA8BF1107DFF_w532_h299.jpg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "我国10万大军跨越海峡要多长时间？张召忠给出这样的回答",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/05/s46586055-102-9987642-123909/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w399_h200_blur_q100_webp/x0.ifengimg.com/res/2020/CE56AA7350E9C3C61086375C007EB974DD3C2978_size35_w320_h200.jpeg.webp"
                )
            )
            list.add(
                createMiniVideoEntity(
                    "号称全球最强“棺材机”，F105战机损失有多少？曾让美军笑不出来",
                    "http://ips.ifeng.com/video19.ifeng.com/video09/2020/08/11/s46788025-102-9987642-105634/index.m3u8?ifsign=1",
                    "https://d.ifengimg.com/w399_h200_blur_q100_webp/x0.ifengimg.com/res/2020/4C317E5C0EC390E3E72A6809FA0DC85D70E7250D_size12_w320_h200.jpeg.webp"
                )
            )
            return list
        }
    }
}