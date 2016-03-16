package uk.co.malbec.machinery.partitions;


import uk.co.malbec.machinery.CategoryGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class LiteralCategoryGroup<CATEGORY, COLLECTOR> implements CategoryGroup<CATEGORY, COLLECTOR> {

    private Map<CATEGORY, COLLECTOR> categories = new HashMap<>();
    private List<CATEGORY> breakpoints;

    private COLLECTOR activeCollector;

    public LiteralCategoryGroup(Supplier<COLLECTOR> creator, CATEGORY... breakpoints){
        this.breakpoints = asList(breakpoints);
        for (CATEGORY breakpoint : breakpoints){
            categories.put(breakpoint, creator.get());
        }
    }

    @Override
    public List<CATEGORY> getKeys() {
         return breakpoints;
    }

    @Override
    public boolean apply(CATEGORY key) {
        activeCollector = categories.get(key);
        return activeCollector != null;
    }

    @Override
    public COLLECTOR current() {
        return activeCollector;
    }

    public COLLECTOR get(CATEGORY key){
        return categories.get(key);
    }
}
