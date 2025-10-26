extends Control

@onready var noteScene = preload("res://scenes/type_game/note/note.tscn")
@onready var note_spawrn = %NoteSpawrn

var spawrned_notes = []

func _ready() -> void:
	PianoManager.note_pressed.connect(_note_pressed)
	_spawrn_note()

func _spawrn_note() -> void:
	for n in spawrned_notes:
		remove_child(n)
	
	var new_note = noteScene.instantiate()
	new_note.init(60)
	new_note.move(note_spawrn.position.x - size.x / 2, note_spawrn.position.y)
	call_deferred("add_child", new_note)
	spawrned_notes.push_back(new_note)

func _note_pressed(note: int) -> void:
	if spawrned_notes.size() == 1 && spawrned_notes.get(0).noteType == note:
		_spawrn_note()
