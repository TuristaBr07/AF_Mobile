package com.example.bibliotecageo;

import java.util.List;

public class LivroAPI {

    private String title;
    private List<String> author_name;
    private Integer first_publish_year;
    private List<String> publisher;

    public String getTitle() {
        return title != null ? title : "Título desconhecido";
    }

    public List<String> getAuthorName() {
        return author_name;
    }

    public Integer getFirstPublishYear() {
        return first_publish_year;
    }

    public List<String> getPublisher() {
        return publisher;
    }

    public String getAutorFormatado() {
        if (author_name != null && !author_name.isEmpty()) {
            return author_name.get(0);
        }
        return "Autor desconhecido";
    }

    public String getEditoraFormatada() {
        if (publisher != null && !publisher.isEmpty()) {
            return publisher.get(0);
        }
        return "Editora desconhecida";
    }

    public String getAnoFormatado() {
        if (first_publish_year != null) {
            return String.valueOf(first_publish_year);
        }
        return "Ano desconhecido";
    }
}
