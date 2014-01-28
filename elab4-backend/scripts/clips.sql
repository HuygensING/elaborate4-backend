# don't publish entries with empty or absent diplomatic transcriptions
update project_entries set publishable=false where id in (select id from project_entries e where e.project_id=17 and 0=(select count(*) from transcriptions where project_entry_id=e.id));
update project_entries set publishable=false where id in (select project_entry_id from transcriptions where text_layer='Diplomatic' and length(body)<20 and project_entry_id in (select id from project_entries where project_id=17));

update projects set text_layers='Transcription;Critical;Translation;Comments' where id=17;
update transcriptions set text_layer='Transcription' where text_layer='Diplomatic' and project_entry_id in (select id from project_entries where project_id=17);
