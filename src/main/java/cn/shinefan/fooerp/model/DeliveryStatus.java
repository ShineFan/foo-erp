package cn.shinefan.fooerp.model;

import java.util.EnumSet;
import java.util.Set;

public enum DeliveryStatus {

    PENDING,
    SHIPPED,
    IN_TRANSIT,
    DELIVERED,
    PARTIAL,
    RETURNED;

    private static final Set<DeliveryStatus> VALID_FROM_PENDING = EnumSet.of(PENDING, SHIPPED, PARTIAL);
    private static final Set<DeliveryStatus> VALID_FROM_SHIPPED = EnumSet.of(IN_TRANSIT, PARTIAL, RETURNED);
    private static final Set<DeliveryStatus> VALID_FROM_IN_TRANSIT = EnumSet.of(DELIVERED, PARTIAL, RETURNED);
    private static final Set<DeliveryStatus> VALID_FROM_DELIVERED = EnumSet.of(RETURNED);
    private static final Set<DeliveryStatus> VALID_FROM_PARTIAL = EnumSet.of(SHIPPED, IN_TRANSIT, DELIVERED, RETURNED);
    private static final Set<DeliveryStatus> VALID_FROM_RETURNED = EnumSet.of(PENDING, SHIPPED);

    public boolean canTransitionTo(DeliveryStatus target) {
        Set<DeliveryStatus> allowed;
        switch (this) {
            case PENDING:
                allowed = VALID_FROM_PENDING;
                break;
            case SHIPPED:
                allowed = VALID_FROM_SHIPPED;
                break;
            case IN_TRANSIT:
                allowed = VALID_FROM_IN_TRANSIT;
                break;
            case DELIVERED:
                allowed = VALID_FROM_DELIVERED;
                break;
            case PARTIAL:
                allowed = VALID_FROM_PARTIAL;
                break;
            case RETURNED:
                allowed = VALID_FROM_RETURNED;
                break;
            default:
                return false;
        }
        return allowed.contains(target);
    }
}
