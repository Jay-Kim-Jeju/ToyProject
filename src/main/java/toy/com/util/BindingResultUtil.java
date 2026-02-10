package toy.com.util;

import org.springframework.validation.BindingResult;

public class BindingResultUtil {

    private BindingResultUtil() {
        // Utility class
    }

    // Returns only the first error message to keep the response simple for UI.
    public static String firstErrorMessage(BindingResult bindingResult) {
        if (bindingResult == null || !bindingResult.hasErrors()) {
            return "";
        }
        return bindingResult.getAllErrors().get(0).getDefaultMessage();
    }
}
