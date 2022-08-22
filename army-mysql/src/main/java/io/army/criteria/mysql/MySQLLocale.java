package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLLocale implements SQLWords {

    ar_AE(" ar_AE"),
    ar_BH(" ar_BH"),
    ar_DZ(" ar_DZ"),
    ar_EG(" ar_EG"),

    ar_IN(" ar_IN"),
    ar_IQ(" ar_IQ"),
    ar_JO(" ar_JO"),
    ar_KW(" ar_KW"),

    ar_LB(" ar_LB"),
    ar_LY(" ar_LY"),
    ar_MA(" ar_MA"),
    ar_OM(" ar_OM"),

    ar_QA(" ar_QA"),
    ar_SA(" ar_SA"),
    ar_SD(" ar_SD"),
    ar_SY(" ar_SY"),

    ar_TN(" ar_TN"),
    ar_YE(" ar_YE"),
    be_BY(" be_BY"),
    bg_BG(" bg_BG"),

    ca_ES(" ca_ES"),
    cs_CZ(" cs_CZ"),
    da_DK(" da_DK"),
    de_AT(" de_AT"),

    de_BE(" de_BE"),
    de_CH(" de_CH"),
    de_DE(" de_DE"),
    de_LU(" de_LU"),

    el_GR(" el_GR"),
    en_AU(" en_AU"),
    en_CA(" en_CA"),
    en_GB(" en_GB"),

    en_IN(" en_IN"),
    en_NZ(" en_NZ"),
    en_PH(" en_PH"),
    en_US(" en_US"),

    en_ZA(" en_ZA"),
    en_ZW(" en_ZW"),
    es_AR(" es_AR"),
    es_BO(" es_BO"),

    es_CL(" es_CL"),
    es_CO(" es_CO"),
    es_CR(" es_CR"),
    es_DO(" es_DO"),

    es_EC(" es_EC"),
    es_ES(" es_ES"),
    es_GT(" es_GT"),
    es_HN(" es_HN"),

    es_MX(" es_MX"),
    es_NI(" es_NI"),
    es_PA(" es_PA"),
    es_PE(" es_PE"),

    es_PR(" es_PR"),
    es_PY(" es_PY"),
    es_SV(" es_SV"),
    es_US(" es_US"),

    es_UY(" es_UY"),
    es_VE(" es_VE"),
    et_EE(" et_EE"),
    eu_ES(" eu_ES"),

    fi_FI(" fi_FI"),
    fo_FO(" fo_FO"),
    fr_BE(" fr_BE"),
    fr_CA(" fr_CA"),

    fr_CH(" fr_CH"),
    fr_FR(" fr_FR"),
    fr_LU(" fr_LU"),
    gl_ES(" gl_ES"),

    gu_IN(" gu_IN"),
    he_IL(" he_IL"),
    hi_IN(" hi_IN"),
    hr_HR(" hr_HR"),

    hu_HU(" hu_HU"),
    id_ID(" id_ID"),
    is_IS(" is_IS"),
    it_CH(" it_CH"),

    it_IT(" it_IT"),
    ja_JP(" ja_JP"),
    ko_KR(" ko_KR"),
    lt_LT(" lt_LT"),

    lv_LV(" lv_LV"),
    mk_MK(" mk_MK"),
    mn_MN(" mn_MN"),
    ms_MY(" ms_MY"),

    nb_NO(" nb_NO"),
    nl_BE(" nl_BE"),
    nl_NL(" nl_NL"),
    no_NO(" no_NO"),

    pl_PL(" pl_PL"),
    pt_BR(" pt_BR"),
    pt_PT(" pt_PT"),
    rm_CH(" rm_CH"),

    ro_RO(" ro_RO"),
    ru_RU(" ru_RU"),
    ru_UA(" ru_UA"),
    sk_SK(" sk_SK"),

    sl_SI(" sl_SI"),
    sq_AL(" sq_AL"),
    sr_RS(" sr_RS"),
    sv_FI(" sv_FI"),

    sv_SE(" sv_SE"),
    ta_IN(" ta_IN"),
    te_IN(" te_IN"),
    th_TH(" th_TH"),

    tr_TR(" tr_TR"),
    uk_UA(" uk_UA"),
    ur_PK(" ur_PK"),
    vi_VN(" vi_VN"),

    zh_CN(" zh_CN"),
    zh_HK(" zh_HK"),
    zh_TW(" zh_TW");

    private final String words;

    MySQLLocale(String words) {
        this.words = words;
    }

    @Override
    public final String render() {
        return this.words;
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLLocale.class.getName(), this.name());
    }

}
