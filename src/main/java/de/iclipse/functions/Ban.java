package de.iclipse.functions;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class Ban {
    private final UUID uuid;
    private final String reason;
    private final UUID authorUuid;
    private final Timestamp banStart;
    private final long banDuration;
    private final Timestamp banEnd;

    public Ban(UUID uuid, String reason, UUID authorUuid, Timestamp banStart, long banDuration, Timestamp banEnd) {
        this.uuid = uuid;
        this.reason = reason;
        this.authorUuid = authorUuid;
        this.banStart = banStart;
        this.banDuration = banDuration;
        this.banEnd = banEnd;
    }

    public Ban(UUID uuid, String reason, UUID authorUuid, long banDuration, Timestamp banEnd){
        this(uuid, reason, authorUuid, Timestamp.from(Instant.now()), banDuration, banEnd);
    }

    public Ban(UUID uuid, String reason, UUID authorUuid, Timestamp banStart, long banDuration){
        this(uuid, reason, authorUuid, banStart, banDuration, Timestamp.from(banStart.toInstant().plusMillis(banDuration)));
    }

    public Ban(UUID uuid, String reason, UUID authorUuid, long banDuration){
        this(uuid, reason, authorUuid, Timestamp.from(Instant.now()), banDuration, Timestamp.from(Instant.now().plusMillis(banDuration)));
    }

    public Ban(UUID uuid, String reason, UUID authorUuid){
        //MaxTimestamp
        this(uuid, reason, authorUuid, Timestamp.from(Instant.now()), -1, Timestamp.valueOf("2038-01-19 03:14:07"));
    }


    public UUID getUuid() {
        return uuid;
    }

    public String getReason() {
        return reason;
    }

    public UUID getAuthorUuid() {
        return authorUuid;
    }

    public Timestamp getBanStart() {
        return banStart;
    }

    public long getBanDuration() {
        return banDuration;
    }

    public Timestamp getBanEnd() {
        return banEnd;
    }

    public boolean isPermanent(){
        return banDuration == -1;
    }
}
