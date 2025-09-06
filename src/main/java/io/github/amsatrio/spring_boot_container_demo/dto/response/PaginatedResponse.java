package io.github.amsatrio.spring_boot_container_demo.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedResponse<T> implements Serializable {
    private List<T> content;
    private boolean last;
    private boolean first;
    private int totalElements;
    private int totalPages;

    // === GETTER
    public List<T> getContent() {
        return content == null ? Collections.emptyList() : Collections.unmodifiableList(content);
    }

    // === SETTER
    public void setContent(List<T> content) {
        this.content = new ArrayList<>(content);
    }

    // === HASHCODE & EQUALS
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + (last ? 1231 : 1237);
        result = prime * result + (first ? 1231 : 1237);
        result = prime * result + totalElements;
        result = prime * result + totalPages;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaginatedResponse<?> other = (PaginatedResponse<?>) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (last != other.last)
            return false;
        if (first != other.first)
            return false;
        if (totalElements != other.totalElements)
            return false;
        if (totalPages != other.totalPages)
            return false;
        return true;
    }

}
