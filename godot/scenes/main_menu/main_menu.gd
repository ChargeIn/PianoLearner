extends Control
	

func _ready():
	if !PianoManager.is_loaded:
		$VBoxContainer/Play.disabled = true
		$VBoxContainer/ErrorLabel.text = "Error: Could not load piano plugin :/"

func _on_play_pressed() -> void:
	PianoManager.start_game();
