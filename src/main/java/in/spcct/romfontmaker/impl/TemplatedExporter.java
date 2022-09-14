package in.spcct.romfontmaker.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import in.spcct.romfontmaker.Exporter;
import in.spcct.romfontmaker.Font;
import in.spcct.romfontmaker.impl.freemarker.BitCount;
import in.spcct.romfontmaker.impl.freemarker.RadixFormatter;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TemplatedExporter implements Exporter {

    public static final String CONFIG_TEMPLATE_DIRECTORY = "template-directory";
    public static final String CONFIG_TEMPLATE = "template";
    public static final String CONFIG_ROM_NAME = "rom-name";


    @Override
    public void export(Font font, OutputStream outputStream, Map<String, String> config) throws IOException {
        //TODO: Initialize config only once
        Configuration freemarkerConfig = setupFreemarkerConfig(config);

        Template template = freemarkerConfig.getTemplate(config.get(CONFIG_TEMPLATE));

        Map<String, Object> model = new HashMap<>();
        model.put("font", font);
        model.put("romName", config.getOrDefault(CONFIG_ROM_NAME, "font_rom"));
        model.put("radixFormatter", new RadixFormatter());
        model.put("bitCount", new BitCount());
        model.put("config", config);
        model.put("date", new Date());

        try (Writer writer = new OutputStreamWriter(outputStream)) {
            template.process(model, writer);
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    @SneakyThrows
    private Configuration setupFreemarkerConfig(Map<String, String> config) {
        Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        freemarkerConfig.setLogTemplateExceptions(true);
        freemarkerConfig.setLocale(Locale.US);
        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates/");
        if (config.containsKey(CONFIG_TEMPLATE_DIRECTORY)) {
            freemarkerConfig.setDirectoryForTemplateLoading(
                    new File(config.get(CONFIG_TEMPLATE_DIRECTORY))
            );
        }
        return freemarkerConfig;
    }

}
