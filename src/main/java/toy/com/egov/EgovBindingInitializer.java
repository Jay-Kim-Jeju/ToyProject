package toy.com.egov;


import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * WebBindingInitializer used by RequestMappingHandlerAdapter.
 * - Applies Bean Validation validator via super.initBinder()
 * - Registers custom editors (date format, string trimming)
 */
public class EgovBindingInitializer extends ConfigurableWebBindingInitializer {

    @Override
    public void initBinder(WebDataBinder binder, WebRequest request) {
        // Apply validator (Bean Validation) and other base initializations
        super.initBinder(binder, request);

        // Set common date format: yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setLenient(false);

        // Register custom editors
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }
}