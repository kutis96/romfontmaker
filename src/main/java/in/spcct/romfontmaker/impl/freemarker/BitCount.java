package in.spcct.romfontmaker.impl.freemarker;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.List;

public class BitCount implements TemplateMethodModelEx {
    @Override
    public Object exec(List args) throws TemplateModelException {
        if (args.size() != 1) {
            throw new TemplateModelException("CeilLog2 may only take one argument");
        }

        int value = ((SimpleNumber) args.get(0)).getAsNumber().intValue();
        if(value == 0)
            return 0;

        return 32 - Integer.numberOfLeadingZeros(value);
    }
}
