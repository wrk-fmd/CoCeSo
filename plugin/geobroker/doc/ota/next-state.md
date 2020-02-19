# One-Time-Action 'nextState'

The one time action 'nextState' might be offered for a unit if at least one incident is assigned.
It depends on the incident type when a 'nextState' action is available.

## JSON received over Geobroker

Example of provided one time action:

```json
{
  "type": "nextState",
  "url": "https://example-server.local/coceso/geo/ota/6f17cbbe-237d-437b-b416-10ef4f0609fd",
  "incidentId": "4d5138fccba2",
  "additionalData": "ZBO"
}
```

If the next state of the action is `DETACHED`, the action will effectively de-assign the unit from the incident:

```json
{
  "type": "nextState",
  "url": "https://example-server.local/coceso/geo/ota/6f17cbbe-237d-437b-b416-10ef4f0609fd",
  "incidentId": "4d5138fccba2",
  "additionalData": "DETACHED"
}
```

## Execution of action

The provided URL has to be called with HTTP POST method.
The returned JSON informs the caller about the result of the operation.

Following properties are provided:
* `success`: *boolean* indicating if the operation was successful.
* `resultCode`: *enum* providing detailed information about the result of the operation.

The result code can have following values:
* `SUCCESS`: If the operation was successful.
* `INVALID_ACTION_ID`: The provided action ID is syntactically incorrect.
* `ACTION_ID_OUTDATED`: The provided action ID is not associated with a valid one-time-action.
* `OPERATION_FAILED`: An unexpected error happened during the execution of the action.
