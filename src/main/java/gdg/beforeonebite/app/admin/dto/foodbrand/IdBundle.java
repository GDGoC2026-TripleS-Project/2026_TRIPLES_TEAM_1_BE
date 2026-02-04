package gdg.beforeonebite.app.admin.dto.foodbrand;

import java.util.Set;

public record IdBundle(
        Set<Long> foodIds,
        Set<Long> brandIds
) {}
