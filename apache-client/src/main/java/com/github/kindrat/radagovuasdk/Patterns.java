package com.github.kindrat.radagovuasdk;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class Patterns {
    public static final Pattern MENU_LINE_PATTERN = Pattern.compile("(<b>)(.*)(</a>)");
    public static final Pattern MENU_URL_PATTERN = Pattern.compile("laws.*=\\d+");
    public static final Pattern MENU_TITLE_PATTERN = Pattern.compile("(.*)(</b>)");

    public static final Pattern LINK_PATTERN = Pattern.compile("(href=\")(\\S+)(\"\\starget=\"_blank\">)" +
            "(<font\\scolor=\"#\\S{6}\">)?([A-Z\\s\\W\\d]+)(</)");
}
