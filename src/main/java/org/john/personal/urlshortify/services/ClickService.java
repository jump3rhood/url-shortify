package org.john.personal.urlshortify.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.john.personal.urlshortify.models.Click;
import org.john.personal.urlshortify.models.Url;
import org.john.personal.urlshortify.repositories.ClickRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClickService {
    private final ClickRepository clickRepository;

    public void save(Url url, HttpServletRequest request){
        Click click = new Click();
        click.setUrl(url);
        click.setClickedAt(LocalDateTime.now());
        click.setIpAddress(request.getRemoteAddr());
        clickRepository.save(click);
    }
}
