package com.clearmind.animeland.login

class LoginViewModelTest {
    private var viewModel: LoginViewModel? = null

    @Mock
    private val repository: GitHubRepository? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this) // required for the "@Mock" annotations

        // Make viewModel a mock while using mock repository and viewContract created above
        viewModel = Mockito.spy(SearchViewModel(repository))
    }

    @Test
    fun searchGitHubRepos_noQuery() {
        val searchQuery: String? = null

        // Trigger
        viewModel.searchGitHubRepos(searchQuery)

        // Validation
        Mockito.verify(repository, Mockito.never()).searchRepos(searchQuery, viewModel)
    }

    @Test
    fun searchGitHubRepos() {
        val searchQuery = "some query"

        // Trigger
        viewModel.searchGitHubRepos(searchQuery)

        // Validation
        Mockito.verify(repository, Mockito.times(1)).searchRepos(searchQuery, viewModel)
    }

    @Test
    fun handleGitHubResponse_Success() {
        val response: Response = Mockito.mock(Response::class.java)
        val searchResponse: SearchResponse = Mockito.mock(SearchResponse::class.java)
        Mockito.doReturn(true).`when`(response).isSuccessful()
        Mockito.doReturn(searchResponse).`when`(response).body()
        val searchResults: List<SearchResult> = Collections.singletonList(SearchResult())
        Mockito.doReturn(searchResults).`when`(searchResponse).getSearchResults()

        // Trigger
        viewModel.handleGitHubResponse(response)

        // Validation
        Mockito.verify(viewModel, Mockito.times(1)).renderSuccess(searchResponse)
    }

    @Test
    fun renderSuccess() {
        val response: Response = Mockito.mock(Response::class.java)
        val searchResponse: SearchResponse = Mockito.mock(SearchResponse::class.java)
        Mockito.doReturn(true).`when`(response).isSuccessful()
        Mockito.doReturn(searchResponse).`when`(response).body()
        Mockito.doReturn(1001).`when`(searchResponse).getTotalCount()

        // Trigger
        viewModel.handleGitHubResponse(response)

        // Validation
        Assert.assertEquals("Number of results: 1001", viewModel.status.get())
    }
}