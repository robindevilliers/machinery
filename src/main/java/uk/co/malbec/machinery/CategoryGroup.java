package uk.co.malbec.machinery;

import java.util.List;

public interface CategoryGroup<CATEGORY, COLLECTOR> extends Referenceable<COLLECTOR> {

    List<CATEGORY> getKeys();

    public boolean apply(CATEGORY key);

    public COLLECTOR get(CATEGORY key);
}
