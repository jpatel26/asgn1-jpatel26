import java.util.*;

final class EventScheduler {
    private PriorityQueue<Event> eventQueue;
    private Map<Entity, List<Event>> pendingEvents;
    private double timeScale;

    public EventScheduler(double timeScale) {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.timeScale = timeScale;
    }


    private void executeAction(Action action) {
        switch (action.kind) {
            case ACTIVITY:
                executeActivityAction(action);
                break;

            case ANIMATION:
                executeAnimationAction(action);
                break;
        }
    }

    private void executeAnimationAction(Action action)
    {
        action.entity.nextImage();
        if (action.repeatCount != 1)
        {
            this.scheduleEvent(action.entity,
                    action.entity.createAnimationAction(Math.max(action.repeatCount - 1, 0)),
                    action.entity.getAnimationPeriod());
        }
    }

    private void executeActivityAction(Action action)
    {
        switch (action.entity.kind)
        {
            case MINER_FULL:
                action.entity.executeMinerFullActivity( action.world, action.imageStore, this);
                break;

            case MINER_NOT_FULL:
                action.entity.executeMinerNotFullActivity( action.world,
                        action.imageStore, this);
                break;

            case ORE:
                action.entity.executeOreActivity( action.world, action.imageStore,
                        this);
                break;

            case ORE_BLOB:
                action.entity.executeOreBlobActivity( action.world,
                        action.imageStore, this);
                break;

            case QUAKE:
                action.entity.executeQuakeActivity(action.world, action.imageStore,
                        this);
                break;

            case VEIN:
                action.entity.executeVeinActivity(action.world, action.imageStore,
                        this);
                break;

            default:
                throw new UnsupportedOperationException(
                        String.format("executeActivityAction not supported for %s",
                                action.entity.kind));
        }
    }


    public void scheduleEvent(Entity entity, Action action, long afterPeriod)
    {
        long time = System.currentTimeMillis() +
                (long)(afterPeriod * timeScale);
        Event event = new Event(action, time, entity);

        eventQueue.add(event);

        // update list of pending events for the given entity
        List<Event> pending = pendingEvents.getOrDefault(entity,
                new LinkedList<>());
        pending.add(event);
        pendingEvents.put(entity, pending);
    }

    public void unscheduleAllEvents(Entity entity)
    {
        List<Event> pending = pendingEvents.remove(entity);

        if (pending != null)
        {
            for (Event event : pending)
            {
                eventQueue.remove(event);
            }
        }
    }


    private void removePendingEvent(Event event)
    {
        List<Event> pending = pendingEvents.get(event.entity);

        if (pending != null)
        {
            pending.remove(event);
        }
    }

    public void updateOnTime( long time)
    {
        while (!eventQueue.isEmpty() &&
                eventQueue.peek().time < time)
        {
            Event next = eventQueue.poll();

            removePendingEvent(next);

            executeAction(next.action);
        }
    }
}