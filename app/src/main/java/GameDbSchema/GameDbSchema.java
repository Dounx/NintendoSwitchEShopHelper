package GameDbSchema;

public class GameDbSchema {
    public static final class USGameTable {
        public static final String NAME = "USGame";

        public static final class Cols {
            public static final String CATEGORY = "category";
            public static final String SLUG = "slug";
            public static final String BUYITNOW = "buyitnow";
            public static final String RELEASE_DATE = "release_date";
            public static final String DIGITALDOWNLOAD = "digitaldownload";
            public static final String FREE_TO_START = "free_to_start";
            public static final String TITLE = "title";
            public static final String SYSTEM = "system";
            public static final String ID = "id";
            public static final String CA_PRICE = "ca_price";
            public static final String NUMBER_OF_PLAYERS = "number_of_players";
            public static final String NSUID = "nsuid";
            public static final String VIDEO_LINK = "video_link";
            public static final String ESHOP_PRICE = "eshop_price";
            public static final String FRONT_BOX_ART = "front_box_art";
            public static final String GAME_CODE = "game_code";
            public static final String BUYONLINE = "buyonline";
        }
    }

    public static final class JPGameTable {
        public static final String NAME = "JPGame";

        public static final class Cols {
            public static final String INITIAL_CODE = "initial_code";
            public static final String NSUID = "nsuid";
            public static final String TITLE_NAME = "title_name";
            public static final String MAKER_NAME = "maker_name";
            public static final String MAKER_KANA = "maker_kana";
            public static final String PRICE = "price";
            public static final String SALES_DATE = "sales_date";
            public static final String SOFT_TYPE = "soft_type";
            public static final String PLATFORM_ID = "platform_id";
            public static final String DL_ICON_FLG = "dl_icon_flg";
            public static final String LINK_URL = "link_url";
            public static final String SCREENSHOT_IMG_FLG = "screenshot_img_flg";
            public static final String SCREENSHOT_IMG_URL = "screenshot_img_url";
        }
    }

    public static final class EUGameTable {
        public static final String NAME = "EUGame";

        public static final class Cols {
            public static final String FS_ID  = "fs_id";
            public static final String CHANGE_DATE = "change_date";
            public static final String URL = "url";
            public static final String TYPE = "type";
            public static final String CLUB_NINTENDO = "club_nintendo";
            public static final String HD_RUMBLE_B = "hd_rumble_b";
            public static final String MULTIPLAYER_MODE = "multiplayer_mode";
            public static final String PRETTY_DATE_S = "pretty_date_s";
            public static final String PLAY_MODE_TV_MODE_B = "play_mode_tv_mode_b";
            public static final String PLAY_MODE_HANDHELD_MODE_B = "play_mode_handheld_mode_b";
            public static final String IMAGE_URL_SQ_S = "image_url_sq_s";
            public static final String PG_S = "pg_s";
            public static final String GIFT_FINDER_DETAIL_PAGE_IMAGE_URL_S = "gift_finder_detail_page_image_url_s";
            public static final String IMAGE_URL = "image_url";
            public static final String ORIGINALLY_FOR_T = "originally_for_t";
            public static final String PRIORITY = "priority";
            public static final String DIGITAL_VERSION_B = "digital_version_b";
            public static final String IMAGE_URL_H2X1_S = "image_url_h2x1_s";
            public static final String AGE_RATING_SORTING_I = "age_rating_sorting_i";
            public static final String PLAY_MODE_TABLETOP_MODE_B = "play_mode_tabletop_mode_b";
            public static final String PUBLISHER = "publisher";
            public static final String IR_MOTION_CAMERA_B = "ir_motion_camera_b";
            public static final String EXCERPT = "excerpt";
            public static final String DATE_FROM = "date_from";
            public static final String PRICE_HAS_DISCOUNT_B = "price_has_discount_b";
            public static final String GIFT_FINDER_DESCRIPTION_S = "gift_finder_description_s";
            public static final String TITLE = "title";
            public static final String SORTING_TITLE = "sorting_title";
            public static final String COPYRIGHT_S = "copyright_s";
            public static final String GIFT_FINDER_CAROUSEL_IMAGE_URL_S = "gift_finder_carousel_image_url_s";
            public static final String PLAYERS_TO = "players_to";
            public static final String GIFT_FINDER_WISHLIST_IMAGE_URL_S = "gift_finder_wishlist_image_url_s";
            public static final String PRETTY_AGERATING_S = "pretty_agerating_s";
            public static final String PLAYERS_FROM = "players_from";
            public static final String AGE_RATING_TYPE = "age_rating_type";
            public static final String GIFT_FINDER_DETAIL_PAGE_STORE_LINK_S = "gift_finder_detail_page_store_link_s";
            public static final String PRICE_SORTING_F = "price_sorting_f";
            public static final String PRICE_LOWEST_F = "price_lowest_f";
            public static final String AGE_RATING_VALUE = "age_rating_value";
            public static final String PHYSICAL_VERSION_B = "physical_version_b";
            public static final String GAME_CATEGORIES_TXT = "game_categories_txt";
            public static final String PLAYABLE_ON_TXT = "playable_on_txt";
            public static final String PRODUCT_CODE_TXT = "product_code_txt";
            public static final String LANGUAGE_AVAILABILITY = "language_availability";
            public static final String SYSTEM_TYPE = "system_type";
            public static final String DATES_RELEASED_DTS = "dates_released_dts";
            public static final String PRETTY_GAME_CATEGORIES_TXT = "pretty_game_categories_txt";
            public static final String TITLE_EXTRAS_TXT = "title_extras_txt";
            public static final String NSUID_TXT = "nsuid_txt";
            public static final String GAME_CATEGORY = "game_category";
            public static final String SYSTEM_NAMES_TXT = "system_names_txt";
        }
    }

    public static final class SupportedCountryTable {
        public static final String NAME = "SupportedCountry";

        public static final class Cols {
            public static final String NAME = "name";
            public static final String CODE = "code";
            public static final String CURRENCY = "currency";
            public static final String BELONG = "belong";
        }
    }

    public static final class GameView {
        public static final String NAME = "Game";

        public static final class  Cols {
            public static final String US_TITLE = "us_title";
            public static final String EU_TITLE = "eu_title";
            public static final String JP_TITLE = "jp_title";
            public static final String GAME_CODE0 = "game_code0";
            public static final String GAME_CODE1 = "game_code1";
            public static final String GAME_CODE2 = "game_code2";
            public static final String LANGUAGE = "language";
            public static final String US_NSUID = "us_nsuid";
            public static final String EU_NSUID = "eu_nsuid";
            public static final String JP_NSUID = "jp_nsuid";
            public static final String DISCOUNT = "discount";
            public static final String ICON_URL0 = "icon_url0";
            public static final String ICON_URL1 = "icon_url1";
            public static final String URL0 = "url0";
            public static final String URL1 = "url1";
            public static final String URL2 = "url2";
            public static final String RELEASE_DATE0 = "release_date0";
            public static final String RELEASE_DATE1 = "release_date1";
            public static final String RELEASE_DATE2 = "release_date2";
            public static final String PLAYER_NUMBER0 = "player_number0";
            public static final String PLAYER_NUMBER1 = "player_number1";
            public static final String CATEGORY0 = "category0";
            public static final String CATEGORY1 = "category1";
        }
    }
}
