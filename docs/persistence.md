# Persistence compatibility policy

The plugin persists per-RS-profile state as JSON through `ConfigManager`:
tracked items (`PersistedItem` list, including `AcquisitionRecord` and
`NotificationRule`), categories (`CategoryData`/`CategoryState`), and the
price cache (`CachedPrice` map). Users upgrade and downgrade the plugin under
this data, and mobile/vanilla sessions run between plugin sessions. Every PR
that changes a persisted shape must follow these rules and say so.

## Rules

1. **Old data loads unchanged.** Every new field must have an explicit safe
   default that means "unknown/legacy" — never a value that fakes knowledge the
   old data didn't have. Example: an acquisition-source tag on old records must
   default to `UNKNOWN`, not to a real source; a suspended-lot flag defaults to
   false. Gson leaves absent fields at their Java defaults, so the Java default
   must *be* the safe default.

2. **Additive changes only.** Add new keys or new fields; never rename a
   field, change a field's type, or repurpose an existing field's meaning.
   Retired fields are simply no longer written (old data containing them is
   ignored harmlessly).

3. **Downgrade tolerance, with a documented limit.** Older plugin versions
   must *load* newer data without failing — additive changes guarantee this,
   since Gson ignores unknown JSON fields. Limitation to be aware of: the
   restore path re-persists items as they load, so an older version rewrites
   the config in its own shape and **drops fields it doesn't know about**. A
   downgrade therefore loses new-field data but never crashes or corrupts what
   it does understand.

4. **Say it in the PR.** Any PR touching a persisted shape includes a short
   compatibility note: what changed, the legacy default, and why it is safe
   under rules 1–3.

5. **Fixtures freeze the legacy shapes.** `PersistenceCompatTest` deserializes
   representative Release-1.3-era JSON through the current models. When a field
   is added, extend the assertions to prove the legacy default; never edit the
   fixture strings themselves — they are historical artifacts.

## Reference

Established by issue #83 for the Release 1.4 source-pricing work (#64); the
schema-changing issues (#12, #59, #60, #61, #70, #72, #79) are bound by it.
