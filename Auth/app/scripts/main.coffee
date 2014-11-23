$ ->
	$form =	$('#login-form').first()
	
	$submit = $form.find('a[data-type=submit]').first()
	$alerts = $ '.alert-container'
	$form.submit (event) ->
		event.preventDefault
		formData = $(this).serialize(); 
		preSubmit()
		setMsg($submit,"Authenticating...")
		$.ajax
			type: "POST"
			url: this.action
			data: formData
			dataType: 'text'
			success: (data) ->
				setMsg($submit,"Success! Redirecting... ")
				msg = JSON.parse(data)
				if (msg.url)
					#console.log "REDR", msg.url
					window.location.replace(msg.url)
				else
					showAlert('danger', 'Something happened parsing response from the server.')
					postSubmit();
			error: (data) ->
				msg = JSON.parse(data.responseText)
				setMsg($submit,"Try again!")
				showAlert('danger', msg.message)
				postSubmit()
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
		$e.fadeOut 'fast', -> $(this).text(msg).fadeIn('fast')

	showAlert = (type, msg) ->
		al = "<div class='alert alert-#{type} error-message text-center' role='alert'>#{msg}</div>"
		setAlert(al)

	setAlert = (al) ->
		$alerts.fadeOut 'fast', -> $(this).html(al).show('fast')
	init = -> 
		enableClick()


	init()