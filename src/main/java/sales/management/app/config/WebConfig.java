package sales.management.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        // Cách B: Nếu bạn muốn Redirect (Trình duyệt tự chuyển hướng đổi URL thành "/login")
        registry.addRedirectViewController("/", "/login");
    }

}
