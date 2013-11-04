from django.shortcuts import render_to_response

def search(request):
    company_name = ""
    search_from = ""
    search_to = ""
    from_to_error = ""
    if request.method == 'POST':
        if(request.POST['company_name'] != ""):
            company_name = request.POST['company_name'] 
        if(request.POST['from'] != "" and request.POST['to']):
            search_from = request.POST['from']
            search_to = request.POST['to']
        elif(request.POST['from'] != ""):
            from_to_error = "Brak miejsca docelowego"
        elif(request.POST['to'] != ""):
            from_to_error = "Brak miejsca poczatkowego" 
    return render_to_response('search_engine/search.html', {
                                                            'company_name': company_name,
                                                            'search_from': search_from,
                                                            'search_to': search_to,
                                                            'from_to_error': from_to_error,
                                                            })