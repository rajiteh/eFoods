$ ->
	$form =	$('#login-form').first()
	$submit = $form.find('a[data-type=submit]').first()
	$alerts = $ '.alert-container'

	$form.keypress (e) -> $form.submit() if e.which is 13

	$form.submit (event) ->
		event.preventDefault
		formData = $(this).serialize(); 
		preSubmit()
		setMsg($submit,"Authenticating...")
		$submit.addClass('btn-warning');
		$.ajax
			type: "POST"
			url: this.action
			data: formData
			dataType: 'text'
			success: (data) ->
				setMsg($submit,"Success! Redirecting... ")
				$submit.addClass('btn-success');
				msg = JSON.parse(data)
				if (msg.url)
					setTimeout ->
						window.location.replace(msg.url)	
					, 500
				else
					showAlert('danger', 'Something happened parsing response from the server. Refresh the page and try again.')
					postSubmit();
			error: (data) ->
				msg = JSON.parse(data.responseText)
				setMsg($submit,"Try again")
				$submit.addClass('btn-danger');
				showAlert('danger', msg.message)
				postSubmit()
				$form.find('input').first().focus()
		false

	preSubmit = ->
		disableClick()
		setAlert('')
		$.each $form[0].elements, (i,e) ->
			$(e).prop "disabled", true

	postSubmit = ->
		enableClick()
		$.each $form[0].elements, (i,e) ->
			$(e).prop "disabled", false

	enableClick = ->
		$submit.click (event) ->
			event.preventDefault
			$form.submit()
			false
	disableClick = ->
		$submit.off()
	setMsg = ($e, msg) ->
		$e.text(msg)
		$e.removeClass('btn-warning');
		$e.removeClass('btn-primary');
		$e.removeClass('btn-danger');
		$e.removeClass('btn-success');


	showAlert = (type, msg) ->
		al = "<div class='alert alert-#{type} error-message text-center' role='alert'>#{msg}</div>"
		setAlert(al)

	setAlert = (al) ->
		$alerts.fadeOut 'fast', -> $(this).html(al).show('fast')
	init = -> 
		enableClick()


	init()